# winkler

A moderately paranoid clojurescript library for generating more entropy. You may need a cryptographically secure RNG but who knows if it's enough.  This is just some extra salt.

## Installation

With Leinengen/Boot:
```
[imaginathansoft/winkler "0.1.0"]
```

With deps.edn:
```
imaginathansoft/winkler {:mvn/version "0.1.0"}
```

To build from source, run in the terminal:

```
git clone https://github.com/ndonolli/winkler.git
```

## Usage

There is one function in the core library named `generate` which will produce a lazy-seq of randomized integers. The sequence will produce until the total bits of entropy is greater than the provided argument amount.

```clojure
;; generate random integers with at least 100 bits of combined entropy
(generate 100) ;; => (1134 -419 16631 -2872 ...)

;; it's a lazy seq, so take as you will.  
;; Each generation does require running computations in order to calculate entropy values.
(take 3 (generate 100)) ;; => (5081 -1092 -4678)
```
## What's it doing?

The entropy gernation technique is ~~ripped off~~ *inspired* by keybase's [more-entropy](https://github.com/keybase/more-entropy) js library.  The library runs a sequence of floating point operations in a given time limit, generating a random integer based off the number of successful operations.  Wheras other techniques involve connecting to the DOM to collect entropy through user events, this technique relies on the entropy state of the machine a la linux's `/dev/random`.

## Should I use this to bootstrap my new crypto startup?

Short answer: No.

Longer answer: This, combined with more cryptographically secure algorithms provided client-side such as `window.crypto`, will provide much, much more security than using psuedo-random number generators such as `Math.random()`. [Read more here](https://stackoverflow.com/questions/578700/how-trustworthy-is-javascripts-random-implementation-in-various-browsers).  Although the V8 implementation moved to the speedy XorShift128+ LSFR, there is an inherent limitation with these types of generators.  While it is suitable for simple generation and games, more entropy is needed for stronger cryptography.

Disclaimer: I am nowhere near an expert in this field.

## Licence

winkler is [MIT licenced](license.txt)