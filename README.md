brokenverifier
==============

This project runs a test against Java 8's verifier, which is more restrictive than in earlier versions of Java.

It consists of a single test, which creates a custom class; this class fails the more 
strict verification tests on Java 8u11 and later.

A fix has been issued against the problem, but has not been released as of Java 8u20.
