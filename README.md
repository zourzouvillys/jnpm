# Java NPM

What the actual fuck, you ask?  Why on earth would _anyone_ want to work with NPM in java?

The answer is they probably wouldn't.    And infact, this project doens't actually implement NPM, it just provides some
tooling around parsing and comparing NPM versions (e.g, the stuff in [semver](https://docs.npmjs.com/misc/semver)).

There is a also a basic dependency resolver - which at least with the test cases matches npm's implementation - and
remote API fetcher and cache to interact with the NPMJS registry. 

All in all, you'll probably never have any reason to use this code.  But a project i was working on called for it, and
seemed like something that deserved it's own public git repo.
