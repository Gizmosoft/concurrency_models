# Async Web Crawler with Event Loop Concurrency

## Problem Statement

Modern applications often need to perform many I/O-bound operations concurrently, such as crawling multiple web pages. Traditional thread-based concurrency can be resource-intensive and complex due to thread management and synchronization issues. This project demonstrates how to efficiently crawl multiple URLs concurrently using Python’s `asyncio` event loop and asynchronous I/O, providing a scalable and lightweight alternative to thread-based approaches.

## Implementation Overview

- **URL Loading:**  
  URLs are read from `urls.txt` and placed into an asynchronous queue.
- **Async Workers:**  
  Multiple asynchronous worker tasks fetch URLs from the queue, download their content using `aiohttp`, parse the HTML for the page title, and write results to `output.txt`.
- **Concurrency Control:**  
  A semaphore limits the number of concurrent HTTP requests, and an async lock ensures safe file writes.
- **Event Loop:**  
  The entire process is orchestrated by an `asyncio` event loop, which schedules and manages all asynchronous tasks.

### Key Files

- `crawler.py`: Main logic for loading URLs, managing the async queue, spawning workers, and running the event loop.
- `file_writer.py`: Utility for appending results to the output file.

## Event Loops and Asyncio

An **event loop** is a programming construct that waits for and dispatches events or messages in a program. In Python, the `asyncio` library provides an event loop for running asynchronous tasks. Instead of blocking on I/O, tasks yield control when waiting (e.g., for network responses), allowing other tasks to run. This enables thousands of concurrent operations with minimal overhead, all within a single thread.

## How Is This Different from Thread-Based Concurrency?

| Event Loop & Async I/O         | Thread-Based Concurrency         |
|-------------------------------|----------------------------------|
| Single-threaded, non-blocking | Multi-threaded, can be blocking  |
| Cooperative multitasking (tasks yield at await points) | Preemptive multitasking (OS can switch threads at any time) |
| No shared memory issues, no need for locks (except for file writes) | Shared memory, requires locks/mutexes for safety |
| Lightweight, low overhead      | Higher memory and CPU overhead   |
| Best for I/O-bound workloads   | Can be better for CPU-bound tasks|

**In this project:**  
- All crawling is done in a single thread using async I/O, making it highly scalable for many URLs.
- No threads are created, so there’s no risk of race conditions except when writing to a file (handled by an async lock).
- The event loop ensures efficient use of resources and simple code structure.

## Requirements

- Python 3.7+
- Install dependencies:  
  ```
  pip install -r requirements.txt
  ```

## Usage

1. Add URLs to `urls.txt` (one per line).
2. Run the crawler:
   ```
   python crawler.py
   ```
3. Results will be saved in `output.txt`. 