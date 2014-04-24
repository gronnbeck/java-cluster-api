# DISCLAIMER
Do not use this API, it was just a school project, its slow, and unmaintained.

JACAL 
=====

Abstract
--------
We present JACAL a fault tolerant Java API for solving combinatorial problems in a distributed and parallelized fashion. This API allows application developers to easily take advantage of the cloud. Features that the API support includes concurrent jobs, prioritized tasks, branch & bound, real time feedback, and checkpointing. It also implements work stealing, task caching and prefetching to increase performance and mask latency among nodes.

Functional Requirements
-----------------------
### Speedups
* Implementation of work stealing (Incl. high-low water marks), among computers proxies on the same space. Will start work stealing after initial prefetching phase. Uses Exponential backoff if a steal fails. 
* Design and implementation of a space network. 

### Support functionality
* Accommodate concurrent jobs. 
* Task priorities (developer can specify n-levels of priority)
* Provide policy files for computers and space(s) with different security requirements.
* Persistent tasks (used for checkpointing if a Space crashes)


#### User friendliness
 * Support for progress report (Real time feedback to the client)
 * Support for run time data in a comma separated list that can be imported into excel.

Key Technical Issues
--------------------
* Have the space fualt tolerant when computer proxies are stealing tasks
* Recovering the state of Space before a crash
* Provide good asynchronous feedback to the user without affecting system performance 
