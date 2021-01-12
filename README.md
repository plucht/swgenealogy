# swgenealogy

Die Relationen und Deduktionen sind in [core.clj](src/genealogy/core.clj) definiert. 

Ausgabe von `lein run`: 
```text
Who are the parents of :luke?
(:padme :anakin)
Who are the children of :anakin?
(:luke :leia)
Who is the mother of :luke?
(:padme)
Who is the mother of :anakin?
(:shmi)
Who is the father of :luke?
(:anakin)
Who is the father of :anakin?
()
Who is the sister of :luke?
(:leia)
Who is the brother of :leia?
(:luke)
Who is the grandparent of :leia?
(:shmi)
Is :shmi a grandparent of :luke?
Yes
Who is the predecessor of :leia?
(:padme :anakin :shmi)
Who is the sister of :leia?
()
Is :shmi a predecessor of :leia?
Yes
Is :padme a grandparent of :leia?
No
How is :padme related to :leia?
(parentof motherof predecessorof)
Show offspring of :anakin and :padme.
(:luke :leia)
Show offsping of :anakin and :shmi.
()
```
