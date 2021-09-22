# Migrating Benerator Projects


## Guidelines

We try to keep execution behaviour and programming interfaces as stable 
as possible between releases. However, if we consider a behaviour as 
wrong, we do change it. 

If you experience generation behaviour changes between Benerator releases, 
please check this migration guide section in its newest version. 
Often, we will provide a flag in the ```<setup>``` attributes which 
makes Benerator keep previous behaviour.


## General Advice For Developers of Custom Components 

If we need to change an interface in order to improve it or to provide 
new features, we always try to adapt its direct child class(es) in a way 
that hides the change from existing implementations.

So, if you are programming custom implementations of Benerator's 
service provider interfaces (SPIs), it makes your code more stable 
against changes, if you inherit from a child class of this interface 
instead of implementing it directly.


## Migration from 1.1.x to 1.2.0

If you did not program custom implementations of Benerator's interfaces 
or classes, existing projects are supposed to work like before (but faster).

If you programmed a custom distribution by implementing the Distribution 
**directly** (and did not inherit from a child class), bad news is that 
your code will not be compatible with Benerator 1.2.0.

Good news is that this is easily fixed: Edit the Java code of your 
custom distribution class and replace the ```ìmplements Distribution```
directive with ```extends AbstractDistribution```.

In case you programmed a custom Sequence, you have two options: 
1. If the sequence fetches and caches all generator data in its applyTo() method, then have ```extends DetachedSequence```
2. else implement the applyTo() method appropriately.