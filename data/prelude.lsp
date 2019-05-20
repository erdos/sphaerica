;; null value is calculated
(def! nil (cdr (quote (1))))

;; first item of list
(def! first (macro a (car (eval (car a)))))
(def! head first)

;; tail of list
(def! rest (lambda (a) (cdr a)))
(def! tail rest)

;; second item of list
(def! second (lambda (a) (car (cdr a))))

;; third item of the list
(def! third  (lambda (a) (car (cdr (cdr a)))))

;; last item of list
(def! last (lambda (b) (if (cdr b) (last (cdr b)) (car b))  ))

;; identity function
(def! identity (lambda ($id) $id))

;; returns key if coll list contains key, null otherwise.
(def! contains? (lambda (coll key) 
                        (if coll 
                            (if (eq (car coll) key) 
                                key 
                                (contains? (cdr coll) key))
                            nil)))

;; maps f on each element of b list.
(def!  map  (lambda (f b) (if b (cons (f (car b)) (map f (cdr b)))b)))

;; fold right	
(def! foldr (lambda (c n x) (if x ( c (car x) (foldr c n (cdr x))) n)))

;; fold right with no zeroth elem
(def! foldr1 (lambda (c x)  (if (cdr x) (c (car x) (foldr1 c (cdr x))) (car x))))

;; reverses list.
(def! reverse* (lambda (a b) (if a (reverse* (cdr a) (cons (car a) b)) b)))
(def! reverse (lambda (x) (reverse* x nil)))

;; fold left
(def! foldl (lambda (c n x) (if x (foldl c (c n (car x)) (cdr x)) n) (car x)))

;; evaluates all item in list
(def! eval-list (macro $l (map (lambda ($x) (eval $x) ) (eval (first $l)))  ))

;; returns a list of its params evaluated. may take any number of params.
(def! list (macro $list-param (eval-list $list-param)))

;; everything is ignored inside a comment macro.
(def! comment (macro x nil))

;; bind helper function. 
(def! bind* (macro xs (eval (list (list (quote lambda) (list (first xs)) (third xs)) (list (quote quote) (eval (second xs)))))))

;; let macro. 
(def! let (macro xs  (if (first xs)  
                          (eval (list (quote bind*) 
                                (car (car xs)) (car (tail (car xs)))  
                                (list (quote let) 
                                      (tail (tail (car xs))) 
                                      (second xs))))
                          (eval (second xs)))))

;; applies f function with the given arguements in the second parameter.
;; (apply f l)
(def! apply (macro x (eval (cons (first x) (eval (second x))))))

;; function composition macro.
(def! -> (macro x (eval (foldr1 (lambda (a b)  (list a b)  ) (reverse x)))))

;; do macro. evalates all forms.
(def! do (macro x  (car (map (lambda (t) (eval t)) (reverse x) ))  ))

;;
;; LOOPS and cycles
;;

;; calls f while the result is inside the collection. returns latest unique result.
(def! while-in! (lambda (f coll) ((lambda (v) (if (contains? coll v) (while-in! f coll) v))(f))))

;; calls f, and adds return value to list if not null. repeats.
(def! cons-while!* (lambda (f li) ((lambda (x) (if x (cons-while!* f (cons x li)) li) )(f))))
(def! cons-while!  (lambda (f) (cons-while!* f nil)))

;; calls fun on elem, and then on the previous result. returns last result not giving nil.
(def! repeat (lambda (fun elem)   (let (x (fun elem))   (if x (repeat fun x) elem))))

;;
;; JAVA INTEROP functions
;;

;; returns java class for name symbol
(def! class-for-name (lambda (name) ((java org.sphaerica.util.MinimaLISP getClassForName (java.lang.String)) &engine name)))

;; converts list to typed array
(def! list->array* (java org.sphaerica.util.MinimaLISP listToArray (org.sphaerica.util.ListItem java.lang.Class)))
(def! Arrays.copyOf (java java.util.Arrays copyOf ([Ljava.lang.Object; int java.lang.Class)))
(def! Array.getLength (java java.lang.reflect.Array getLength (java.lang.Object)))

(def! length (lambda (x) (Array.getLength (quote 1) x) ))
(def! dummy (quote 1))

; converts list to typed array.
; example: (list->array (quote (1 2)) (class-for-name (quote [Ljava.lang.String;)))
(def! list->array (lambda (x y) (Arrays.copyOf dummy x (length x) (class-for-name y) )))


(def! t0 (lambda() (list->array (quote (1 2 3 4))  (quote [Ljava.lang.Object;) ) ))
(def! t1 (lambda() (list->array (quote (1 2 3 4))  (quote [Ljava.lang.String;) ) ))

(comment
(def! multifun 
  (macro $m
     (let (argname (car $m)
           funbody (car (cdr $m))
           f        (list (quote lambda) (list argname) funbody)  
           ef (eval f) 
          )
          
       (lambda ()  (ef nil)  )
     ) 
  )
)
)

;; MATH
(def! parseDouble* (java java.lang.Double parseDouble (java.lang.String)))
(def! double (lambda (s)     (  parseDouble* (quote dummy) s) ))
(def! double0 (double (quote 0)))


(def! test1 (lambda ()  (java-cons org.sphaerica.math.Vector3D ())  ))
(def! test2 (lambda ()  (java-cons org.sphaerica.math.Vector3D (double double double))  ))

(def! getConstructor  (java java.lang.Class getConstructor ([Ljava.lang.Class;)))
(def! newInstance (java java.lang.reflect.Constructor newInstance ([Ljava.lang.Object;)) )

(def! -args (quote (org.sphaerica.math.Vector3D (double double double))))

(def! java-ctr
  (macro -args
    (let(cls (class-for-name     (first  -args)) ; class
       acl (map class-for-name (second -args)) ; args class list
       aca (list->array acl (quote [Ljava.lang.Class;)) ; args class array
       ctr (getConstructor cls aca)
    ) 
    (eval (list (quote macro) (quote $ctr) (list (quote newInstance) ctr (quote (eval-list $ctr)) )))
  )))

(def! test1 (java-ctr org.sphaerica.math.Vector3D (double double double)) )


(def! -cname (car -args)) ; class name
(def! -clss  (class-for-name -cname));class for name
(def! -cargs (car (cdr -args))) ; constructor param type names
(def! -cargsc (map class-for-name -cargs)) ; ctor param type classes
(def! -cargs-array (list->array -cargsc (quote [Ljava.lang.Class;))  ) ; constructor param types 

(def! -ctor   (getConstructor -clss -cargs-array)    )

(def! obi (newInstance -ctor (list double0 double0 double0)))

;(def! -ctor   ( -ctor* -clss  (list->array (quote (double double double))   (class-for-name (quote java.lang.Class)))     )   )



;; creates LinkedList instance
;(def! LinkedList. (java-cons java.util.LinkedList ()))

;; creates ArrayList instance
;(def! ArrayList. (java-cons java.util.ArrayList ()))
(def! .add (java java.util.Collection add (java.lang.Object)))
(def! .isEmpty (java java.util.Collection isEmpty ()))
(def! .size (java java.util.Collection size ()))




;; creates and array of given type and size.
(def! create-array
	(lambda (t n)
		(let ( type (class-for-name t)
			   ctor (java java.lang.reflect.Array newInstance (java.lang.Class int)))
			 (ctor (quote 0) type n))))

;; calls toArray of Collection and returns a typed array of type t.
(def! .toArray 
	  (lambda (x t)
		     ( (java java.util.Collection toArray ([Ljava.lang.Object;)) x (create-array t (.size x)))))

 (java java.util.Collection toArray ([Ljava.lang.Object;))

(def! Object.equals (java java.lang.Object equals (java.lang.Object)))
(def! bool-true (Object.equals (quote 1) (quote 1)))
(def! bool-false (Object.equals (quote 1) (quote 2)))
(def! bool-true? (lambda (x) (if (eq bool-true x) x nil)))

(def! Iterable.iterator* (java java.lang.Iterable iterator ()))
(def! Iterator.hasNext* (java java.util.Iterator hasNext ()))
(def! Iterator.next* (java java.util.Iterator next ()))

(def! Iterable->list (lambda (x)
   (let (iter (Iterable.iterator* x)
         funi (lambda (y) (if (bool-true? (Iterator.hasNext* iter))
                              (cons (Iterator.next* iter) y)
                              nil)))
         (repeat funi nil))))

;; creates a list of FreePoint instances on the worksheet
(def! freepoints* (java org.sphaerica.worksheet.WorksheetWorker freepoints ()))
(def! freepoints (lambda () (Iterable->list (freepoints* &worker))))

;;
;; GEOMETRY request functions
;;

;; user selection functions. these functions will wait for user input.
(def! select-point  (lambda () ((java org.sphaerica.worksheet.WorksheetWorker point ())  &worker)))
(def! select-circle (lambda () ((java org.sphaerica.worksheet.WorksheetWorker circle ()) &worker)))
(def! select-curve  (lambda () ((java org.sphaerica.worksheet.WorksheetWorker curve ())  &worker)))

;; waits for user to select other curve than given one.
(def! select-other-curve (lambda (p)   ((lambda (t) (if (eq p t) (select-other-curve p) t)  ) (select-curve))))

;; waits to user to select other point than p.
(def! select-other-point (lambda (p) (while-in! select-point (list p))))

;; waits for user to select two different points.
(def! select-two-points (lambda ()  (let (p (select-point) q (select-other-point p))  (list p q) )))

;;
;; GEOMETRY builder functions
;;

;; removes object from the sphere.
(def! remove (lambda ($z) (if $z ((java org.sphaerica.worksheet.WorksheetWorker remove (org.sphaerica.worksheet.SphericalObject)) &worker $z) $z)))

(def! add (lambda ($z) (if $z ((java org.sphaerica.worksheet.WorksheetWorker add (org.sphaerica.worksheet.SphericalObject)) &worker $z) $z)))

;; sets the visibility of given object to visible.
(def! show (lambda (x) (if x ((java org.sphaerica.worksheet.AbstractSphericalObject show ()) x) x) ))

;; sets the visibility of given object to hidden.
(def! hide (lambda (x) (if x ((java org.sphaerica.worksheet.AbstractSphericalObject hide ()) x) x) ))

;; creates a circle object on the sphere with a given origo and perimeter points.
(def! circle (lambda (origo perimeter) ((java org.sphaerica.worksheet.WorksheetWorker circle (org.sphaerica.worksheet.AbstractPoint org.sphaerica.worksheet.AbstractPoint)) &worker origo perimeter)))

;; creates a line on the sphere with the given center point.
(def! line (lambda (a) ((java org.sphaerica.worksheet.WorksheetWorker line (org.sphaerica.worksheet.AbstractPoint)) &worker a)))

;; creates a midpoint on sphere with the two endpoints.
(def! midpoint (lambda (a b) ((java org.sphaerica.worksheet.WorksheetWorker midpoint (org.sphaerica.worksheet.AbstractPoint org.sphaerica.worksheet.AbstractPoint)) &worker a b)))

;; creates antipodal point on the sphere for the given point.
(def! antipode (lambda (a) ((java org.sphaerica.worksheet.WorksheetWorker antipode (org.sphaerica.worksheet.AbstractPoint)) &worker a)))

;; creates line segment on the sphere.
(def! segment (lambda (a b) ((java org.sphaerica.worksheet.WorksheetWorker segment (org.sphaerica.worksheet.AbstractPoint org.sphaerica.worksheet.AbstractPoint)) &worker a b)))

;; creates an intersection point of the two curves on the sphere.
(def! intersect (lambda (a b) ((java org.sphaerica.worksheet.WorksheetWorker intersection (org.sphaerica.worksheet.AbstractCurve org.sphaerica.worksheet.AbstractCurve)) &worker a b)))

;; polygon helper function. accepts a java array of AbstractPoint instances.
(def! poly (lambda (pts) ((java org.sphaerica.worksheet.WorksheetWorker poly ([Lorg.sphaerica.worksheet.AbstractPoint;)) &worker pts)))

(def! Vector3D.double3 (lambda (a b c) (   (java org.sphaerica.math.Vector3D create (double double double)) (quote dummy) a b c)  ) )
(def! Vector3D. (lambda (a)  ((java org.sphaerica.math.Vector3D create (org.sphaerica.math.Vector3D)) (quote dummy) a)     ))
(def! Vector3D.add (java org.sphaerica.math.Vector3D add (org.sphaerica.math.Vector3D)) )
(def! Vector3D.subtract (java org.sphaerica.math.Vector3D subtract (org.sphaerica.math.Vector3D)) )

(def! Vector3D.scale (java org.sphaerica.math.Vector3D scaleBy (double)) )
(def! Vector3D.scaleInverse (java org.sphaerica.math.Vector3D inverseScaleBy (double)) )
(def! Vector3D.normalize (java org.sphaerica.math.Vector3D normalize ()) )
(def! Vector3D.normalizeThis (java org.sphaerica.math.Vector3D normalizeThis ()) )
(def! Vector3D/zero (Vector3D.double3 double0 double0 double0) )


(def! Vector3D.distanceSquareTo (java org.sphaerica.math.Vector3D distanceSquareTo (org.sphaerica.math.Vector3D)) )

(def! AbstractPoint.getLocation (java org.sphaerica.worksheet.AbstractPoint getLocation ()))
(def! FreePoint.setLocation (java org.sphaerica.worksheet.FreePoint setLocation (org.sphaerica.math.UnitVector)))

;; PHYSICS modelling
(def! physics (lambda ()
   (let(pts         (freepoints)
        locs        (map AbstractPoint.getLocation pts)        
        forcene     (lambda (p q) 
                       (Vector3D.scaleInverse 
                          (Vector3D.normalizeThis 
                             (Vector3D.subtract (Vector3D. q) p))
                          (Vector3D.distanceSquareTo p q)))
        forces-res  (lambda (p q) (if (eq p q) Vector3D/zero (forcene p q)))    
        force       (lambda (p)  (foldr (lambda (x y) 
                                          (Vector3D.add y (forces-res x p))) 
                                        (Vector3D. p)
                                        locs))
        action      (map (lambda (j) 
                            (FreePoint.setLocation j 
                               (Vector3D.normalize 
                               (force (AbstractPoint.getLocation j))))) pts ))
       nil)))


