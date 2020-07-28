# winkler

A moderately paranoid clojurescript library for generating more entropy.

## Usage

As of now, you must build from source and include manually with other projects.  This library has not yet been published to clojars or npm, but will be soon.

To begin, run this command:

`git clone https://github.com/ndonolli/winkler.git`


There is one function in the core library named `generate` which will produce a lazy-seq of randomized integers. The sequence will produce until the total bits of entropy is greater than the provided argument amount.

```clojure
;; generate random integers with at least 100 bits of combined entropy
(generate 100) ;; => (1134 -419 16631 -2872 ...)

;; it's a lazy seq, so take as you will.  
;; Each generation does require running computations in order to calculate entropy values.
(take 3 (generate 100)) ;; => (5081 -1092 -4678)
```

## Licence

winkler is [MIT licenced](license.txt)