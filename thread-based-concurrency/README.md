# Multithreaded File Compressor
CLI tool in Java that compresses multiple files in parallel using ThreadPoolExecutor (via ExecutorService)

## Tech Stack
•	**Language:** Java 11+  
•	**Compression Algorithm:** GZIP (use GZIPOutputStream)  
•	**Concurrency Tool:** ExecutorService with a fixed thread pool

## Background
### Thread-Pool Based Concurrency
A pattern where a fixed number of threads are created in advance and reused to execute multiple tasks concurrently. It avoids the overhead of creating a new thread for every task.

**Why use it?**
-	Creating threads is expensive
-	Too many threads cause resource exhaustion or poor performance
-	Reusing threads is an efficient approach

**ExecutorService** Interface is the main abstraction to manage and run tasks using thread pools. It provides lifecycle methods such as submit(), shutdown(), awaitTermination()  
**Executors** Factory class provides methods to create different types of thread pools  
**Runnable** are the tasks that executed by the ExecutorService and do not return a result  
**Callable** are the tasks that return a result  
**Future** represents the result of an asynchronous computation like a *Promise* and ensures we get a value later. When we submit a Callable task to the executor, we get a Future  
Internally, all thread pools are instances of **ThreadPoolExecutor**

**Best practices:**
-	For CPU bound tasks: threads = # of CPU cores
-	For I/O bound tasks: threads = 2 or 4 * # of CPU cores
-	Always shutdown and awaitTermination the executor service

## Goal
We are trying to create a CLI-based multithreaded file compressor system to compress mutliple files at once using the Thread Pool

## Implementation
### Compressor
* Interface that defines the `void compress()` method

### GzipCompressor
* We are using the GZIP algorithm to compress the files in the project
* `GzipCompressor` class implements the `Compressor ` interface
* We create the output directory to store the compressed files in the constructor
* We implement the `compress()` method from the `Compress` interface
* We use `BufferedInputStream` to parse the input files in chunks of 16KB using a byte[]
* We use `GZIPOutputStream` to write the compressed output to the output files

### Driver
* Gets all the files from the resources/data folder and stores in an array
* Initialize `ExecutorService` with the thread pool equal to the number of logical CPU cores available to the JVM
* For each file in the array, initiate the `compress()` method using the executor
* Close the executor after processing

## Result
Below is the expected output:
```
========== File Compressor CLI ============


Number of logical CPU cores available to JVM: 12
[pool-1-thread-2] successfully compressed pg174.txt-> processed/pg174.txt.gz
[pool-1-thread-5] successfully compressed pg2641.txt-> processed/pg2641.txt.gz
[pool-1-thread-8] successfully compressed pg69087-images-kf8.mobi-> processed/pg69087-images-kf8.mobi.gz
[pool-1-thread-4] successfully compressed pg2554.txt-> processed/pg2554.txt.gz
[pool-1-thread-6] successfully compressed pg2701.txt-> processed/pg2701.txt.gz
[pool-1-thread-3] successfully compressed pg20228.txt-> processed/pg20228.txt.gz
[pool-1-thread-7] successfully compressed pg69087-h.zip-> processed/pg69087-h.zip.gz
[pool-1-thread-1] successfully compressed pg100.txt-> processed/pg100.txt.gz
```