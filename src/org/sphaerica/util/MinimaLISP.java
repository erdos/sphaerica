package org.sphaerica.util;

import java.io.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * A small lisp interpreter.
 */
public class MinimaLISP {

    private final Env rootBindings = new Env(null);

    {
        rootBindings.sym.put("&engine", new ContainerItem<MinimaLISP>(this));
    }

    private boolean isIdentifierChar(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || "!@#$%^&*_+-=<>?/~.[];".contains(c + "");
    }

    private Item parse(Reader re) throws IOException {
        final Stack<Item> stack = new Stack<Item>();
        char c = ' ';
        while (true) {
            while (Character.isWhitespace(c))
                c = (char) re.read();
            if (c == (char) -1)
                break;
            else if (c == ';') {
                while (c != (char) -1 && c != '\n')
                    c = (char) re.read();
                continue;
            } else if (c == '(') {
                stack.push(OpCodes.ARGLIST_END);
            } else if (c == ')') {
                ListItem li = null;
                for (Item it = stack.pop(); it != OpCodes.ARGLIST_END; it = stack
                        .pop())
                    li = new ListItem(it, li);
                // if(li == null) li = new ListItem(null, null);
                if (stack.empty())
                    return li;
                stack.push(li);
            } else if (isIdentifierChar(c)) {
                StringBuilder sb = new StringBuilder();
                while (isIdentifierChar(c) && c != (char) -1) {
                    sb.append(c);
                    c = (char) re.read();
                }
                stack.push(new Symbol(sb.toString()));
                continue;
            } else
                throw new RuntimeException("unknown char: " + c);
            c = (char) re.read();
        }
        if (stack.size() == 1)
            return stack.pop();
        if (stack.size() == 0)
            return null;
        throw new RuntimeException("missing ) " + stack.size());
    }

    enum OpCodes implements Item {
        CALL, IF, EVAL, CONS, CAR, CDR, DEF, POP_BINDINGS, PUSH, ARGLIST_END, CALL2, EQ;

        @Override
        public Item apply(ItemVisitor v) {
            return v.visitSym(new Symbol("OPCODE-" + this.name()));
        }
    }

    private ListItem li(Item c, ListItem li) {
        return new ListItem(c, li);
    }

    private static void push(Stack<Item> stack, Item... items) {
        for (Item i : items)
            stack.push(i);
    }

    private Object call(String s) {
        try {
            return eval(parse(new StringReader(s)), rootBindings);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Item eval(final Item it, Env root) {
        if (it == null)
            return null;

        final Stack<Env> envs = new Stack<Env>();
        envs.push(root);

        final Stack<Item> stack = new Stack<Item>(), ops = new Stack<Item>();

        stack.push(it);
        ops.add(OpCodes.EVAL);

        while (!ops.isEmpty()) {
            final Item cmd = ops.pop();

            if (cmd == OpCodes.PUSH)
                stack.push(ops.pop());
            else if (cmd == OpCodes.EVAL)
                stack.pop().apply(new ItemVisitor() {
                    Item visitSym(Symbol s) {
                        stack.push(envs.peek().lookup(s.obj));
                        return s;
                    }

                    Item visitCon(ContainerItem<?> it) {
                        stack.push(it);
                        return it;
                    }

                    Item visitList(ListItem li) {
                        if (li.head() instanceof Symbol) {
                            String head = ((Symbol) li.head()).obj;
                            if (head.equals("macro")) {
                                ops.push(li(new ContainerItem<Env>(null),
                                        li.tail()));
                                ops.push(OpCodes.PUSH);
                                return null;
                            } else if (head.equals("lambda")) {
                                final Item env = new ContainerItem<Env>(
                                        new Env(envs.peek()));
                                ops.push(li(env, li.tail()));
                                ops.push(OpCodes.PUSH);
                                return null;
                            } else if (head.equals("quote")) {
                                push(ops, li.tail().head(), OpCodes.PUSH);
                                return null;
                            } else if (head.equals("if")) {
                                push(ops, OpCodes.EVAL, OpCodes.IF,
                                        OpCodes.EVAL, li.tail().head(),
                                        OpCodes.PUSH, li.tail().tail().head(),
                                        OpCodes.PUSH, li.tail().tail().tail()
                                                .head(), OpCodes.PUSH);
                                return null;
                            } else if (head.equals("def!")) {
                                push(ops, OpCodes.DEF, li.tail().head(),
                                        OpCodes.PUSH, OpCodes.EVAL, li.tail()
                                                .tail().head(), OpCodes.PUSH);
                                return null;
                            } else if (head.equals("car")) {
                                push(ops, OpCodes.CAR, OpCodes.EVAL, li.tail()
                                        .head(), OpCodes.PUSH);
                                return null;
                            } else if (head.equals("cdr")) {
                                push(ops, OpCodes.CDR, OpCodes.EVAL, li.tail()
                                        .head(), OpCodes.PUSH);
                                return null;
                            } else if (head.equals("cons")) {
                                push(ops, OpCodes.CONS, OpCodes.EVAL, li.tail()
                                        .head(), OpCodes.PUSH, OpCodes.EVAL, li
                                        .tail().tail().head(), OpCodes.PUSH);
                                return null;
                            } else if (head.equals("eval")) {
                                push(ops, OpCodes.EVAL, OpCodes.EVAL, li.tail()
                                        .head(), OpCodes.PUSH);
                                return null;
                            } else if (head.equals("eq")) {
                                push(ops, OpCodes.EQ, OpCodes.EVAL, li.tail()
                                        .head(), OpCodes.PUSH, OpCodes.EVAL, li
                                        .tail().tail().head(), OpCodes.PUSH);
                                return null;
                            } else if (head.equals("java")) {
                                String className = ((Symbol) li.tail().head()).obj;

                                String mthName = ((Symbol) li.tail().tail()
                                        .head()).obj;
                                ListItem argsList = (ListItem) li.tail().tail()
                                        .tail().head();

                                try {
                                    int i = 0;
                                    for (ListItem l = argsList; l != null; l = l
                                            .tail())
                                        if (l.head() != null)
                                            i++;

                                    final Class<?>[] paramTypes = new Class[i];

                                    ListItem l = argsList;
                                    for (int j = 0; j < i; j++) {
                                        String cname = ((ContainerItem<String>) l
                                                .head()).obj;
                                        paramTypes[j] = getClassForName(cname);
                                        l = l.tail();
                                    }

                                    final Class<?> c = Class.forName(className);
                                    final AccessibleObject m = c.getMethod(
                                            mthName, paramTypes);
                                    ContainerItem<?> meth = new ContainerItem<AccessibleObject>(
                                            m);
                                    push(ops, li(meth, null), OpCodes.PUSH);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }

                                return null;
                            } else if (head.equals("dump")) {
                                System.out.println("DUMP");
                                System.out.println(" "
                                        + pprint(li.tail().head()));
                                push(ops, null, OpCodes.PUSH);
                                return null;
                            }
                        }
                        push(ops, OpCodes.POP_BINDINGS, OpCodes.CALL); // call
                        push(ops, OpCodes.EVAL, li.head(), OpCodes.PUSH); // head
                        push(ops, li.tail(), OpCodes.PUSH); // arguments
                        return null;
                    }
                });
            else if (cmd == OpCodes.IF) {
                Item cond = stack.pop(); // condition

                if (cond != null
                        && (!(cond instanceof ContainerItem<?>) || ((ContainerItem<Object>) cond).obj != null)) {
                    Item then = stack.pop(); // then branch
                    stack.pop(); // else branch
                    stack.push(then);
                } else
                    stack.pop(); // then branch
            } else if (cmd == OpCodes.CALL) {
                final Item pop = stack.pop();
                if (pop instanceof OpCodes) {
                    envs.push(envs.peek());
                    ops.push(pop);
                } else if (pop instanceof ListItem) {
                    final ListItem args = (ListItem) stack.pop();

                    final ListItem lambda = (ListItem) pop;
                    final ContainerItem<?> closure = ((ContainerItem<?>) lambda
                            .head());

                    if (closure.obj == null) { // macro
                        final Item body = lambda.tail().tail().head();

                        final Symbol argName = (Symbol) lambda.tail().head();
                        final Env cenv = new Env(envs.peek());
                        cenv.sym.put(argName.obj, args);
                        envs.push(cenv);
                        push(ops, OpCodes.EVAL, body, OpCodes.PUSH);
                    } else { // method or function
                        push(ops, OpCodes.CALL2);
                        push(ops, pop, OpCodes.PUSH);
                        for (ListItem l = args; l != null; l = l.tail())
                            push(ops, OpCodes.EVAL, l.head(), OpCodes.PUSH);
                        push(ops, OpCodes.ARGLIST_END, OpCodes.PUSH);
                    }
                } else
                    throw new RuntimeException("can not call: "
                            + pop.getClass() + "\t" + pprint(pop));
            } else if (cmd == OpCodes.CALL2) { // function call.
                processOpCall2(stack, ops, envs);
            } else if (cmd == OpCodes.CAR) {
                ListItem li = (ListItem) stack.pop();
                stack.push(li == null ? new ContainerItem<Object>(null) : li
                        .head());
            } else if (cmd == OpCodes.CDR) {
                ListItem li = ((ListItem) stack.pop());
                stack.push(li == null ? new ContainerItem<Object>(null) : li
                        .tail());
            } else if (cmd == OpCodes.POP_BINDINGS) {
                envs.pop();
            } else if (cmd == OpCodes.CONS) {
                Item head = stack.pop();
                ListItem tail = (ListItem) stack.pop();
                stack.push(new ListItem(head, tail));
            } else if (cmd == OpCodes.DEF) {
                Symbol head = (Symbol) stack.pop();
                Item val = stack.pop();
                rootBindings.sym.put(head.obj, val);
                stack.push(val);
            } else if (cmd == OpCodes.EQ) {
                final Item a = stack.pop(), b = stack.pop();
                stack.push((a == null) ? null : (a.equals(b) ? a : null));
            } else
                throw new RuntimeException(">> " + pprint(cmd));
        }
        return stack.pop();
    }

    @SuppressWarnings("unchecked")
    private <T> T[] listToArray(ListItem lli, Class<T> cls) {
        ArrayList<Object> ll = new ArrayList<Object>(3);
        for (ListItem li = lli; li != null; li = li.tail()) {
            Object o = ((ContainerItem<?>) li.head()).obj;
            ll.add(o);
        }
        return ll.toArray((T[]) Array.newInstance(cls, 0));
    }

    private void processOpCall2(Stack<Item> stack, Stack<Item> ops,
                                Stack<Env> envs) {
        final ListItem pop = (ListItem) stack.pop();

        if (((ContainerItem<?>) pop.head()).obj instanceof Env) {
            final Env cenv = new Env((Env) ((ContainerItem<?>) pop.head()).obj);
            final Item body = pop.tail().tail().head();
            ListItem args = (ListItem) pop.tail().head(); // argument
            // list
            for (Item item = stack.pop(); item != OpCodes.ARGLIST_END; item = stack
                    .pop()) {
                cenv.sym.put(((Symbol) args.head()).obj, item);
                args = args.tail();
            }
            envs.push(cenv);
            push(ops, OpCodes.EVAL, body, OpCodes.PUSH);
        } else if (((ContainerItem<?>) pop.head()).obj instanceof Method) {
            final Method meth = (Method) ((ContainerItem<?>) pop.head()).obj;
            Object that = ((ContainerItem<?>) stack.pop()).obj;
            Class<?>[] paramtypes = meth.getParameterTypes();
            Object[] arguments = new Object[paramtypes.length];
            int i = 0;
            for (Item item = stack.pop(); item != OpCodes.ARGLIST_END; item = stack
                    .pop()) {
                if (item instanceof ContainerItem<?>)
                    arguments[i] = ((ContainerItem<?>) item).obj;
                else if (item instanceof ListItem) {
                    if (paramtypes[i].isArray())
                        arguments[i] = listToArray((ListItem) item,
                                paramtypes[i].getComponentType()); // meth.getParameterTypes()[i].getComponentType()
                    else
                        arguments[i] = listToArray((ListItem) item,
                                Object.class); // meth.getParameterTypes()[i].getComponentType()
                } else if (item == null)
                    arguments[i] = null;
                else
                    throw new RuntimeException("unknown type for arg:" + item);
                i++;
            }
            try {
                final Object ret = meth.invoke(that, arguments);
                Object obi = (meth.getReturnType() == Void.TYPE) ? that : ret;
                envs.push(new Env(envs.peek()));
                push(ops, new ContainerItem<Object>(obi), OpCodes.PUSH);
            } catch (Exception e) {
                if (e.getCause() instanceof InterruptedException) {
                    ops.clear();
                    stack.clear();
                    stack.add(null);
                    return;
                }
                System.out.println(">>\t" + meth.getName() + "\t" + that);
                for (Object o : arguments)
                    if (o instanceof Object[])
                        for (Object k : ((Object[]) o))
                            System.out
                                    .println("\t\t" + k + "\t" + k.getClass());
                    else

                        System.out.println("\t" + o);
                throw new RuntimeException(e);
            }
        }
    }

    public void prelude() throws IOException {
        final InputStream is = getClass().getResourceAsStream("/prelude.lsp");
        final Reader re = new InputStreamReader(is);

        for (Item it = parse(re); it != null; it = parse(re))
            eval(it, rootBindings);
    }

    public void addBinding(String key, Object obj) {
        rootBindings.sym.put(key, new ContainerItem<Object>(obj));
    }

    /**
     * Pretty print an s-expression.
     */
    private String pprint(Item i) {
        if (i == null)
            return "null";

        final StringBuilder b = new StringBuilder();

        i.apply(new ItemVisitor() {
            Item visitSym(Symbol s) {
                b.append(":").append(s.obj);
                return s;
            }

            Item visitList(ListItem l) {
                if (l.head() == null && l.tail() == null) {
                    b.append("()");
                    return l;
                }
                b.append("(");

                for (int i = 0; (i < 8) && l != null; i++) {
                    if (l.head() != null)
                        l.head().apply(this);
                    else
                        b.append("null");
                    l = l.tail();
                    b.append(" ");
                }
                if (l != null)
                    b.append("...");
                b.append(")");
                return l;
            }

            Item visitCon(ContainerItem<?> c) {
                b.append("=").append(c.obj);
                return c;
            }
        });
        return b.toString();
    }

    private final Map<String, Class<?>> cnames = new HashMap<String, Class<?>>();

    {
        for (Class<?> c : new Class[]{byte.class, short.class, int.class,
                long.class, double.class, float.class, char.class})
            cnames.put(c.getName(), c);
    }

    private Class<?> getClassForName(String name)
            throws ClassNotFoundException {
        Class<?> c = cnames.get(name);
        if (c == null)
            cnames.put(name, c = Class.forName(name));
        return c;
    }

    public Object callprint(String q) {
        Item it = (Item) call(q);
        System.out.println(pprint(it));
        return it;
    }
}

class Env {
    final Map<String, Item> sym = new HashMap<String, Item>();
    private final Env outer;

    Env(Env out) {
        this.outer = out;
    }

    Item lookup(String key) {
        if (sym.containsKey(key))
            return sym.get(key);
        else if (outer == null)
            throw new RuntimeException("no binding for key: " + key);
        else
            return outer.lookup(key);
    }
}

interface Item {
    Item apply(ItemVisitor v);
}

abstract class ItemVisitor {
    Item visitSym(Symbol s) {
        return s;
    }

    Item visitCon(ContainerItem<?> c) {
        return c;
    }

    Item visitList(ListItem li) {
        return li;
    }
}

class ContainerItem<T> implements Item {
    public final T obj;

    ContainerItem(T o) {
        this.obj = o;
    }

    public String toString() {
        return "$" + obj;
    }

    public Item apply(ItemVisitor v) {
        return v.visitCon(this);
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof ContainerItem<?>))
            return false;
        Object b = ((ContainerItem<?>) o).obj;
        return (this.obj == null) ? (b == null) : this.obj.equals(b);
    }
}

class Symbol extends ContainerItem<String> {
    Symbol(String s) {
        super(s);
    }

    public String toString() {
        return ":" + super.obj;
    }

    public Item apply(ItemVisitor v) {
        return v.visitSym(this);
    }

    public boolean equals(Object other) {
        if (!(other instanceof Symbol))
            return false;
        return ((Symbol) other).obj == this.obj;
    }
}

class ListItem implements Item {
    private final Item head;
    private final ListItem tail;

    ListItem(Item h, ListItem t) {
        this.head = h;
        this.tail = t;
    }

    public Item head() {
        return this.head;
    }

    public ListItem tail() {
        return this.tail;
    }

    public Item apply(ItemVisitor v) {
        return v.visitList(this);
    }
}
