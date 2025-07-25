import asyncio
import aiohttp
from bs4 import BeautifulSoup
from pathlib import Path
from file_writer import write_to_file

# Parse urls from urls.txt
def load_urls(filename="urls.txt"):
    with open(filename, "r") as f:
        urls = [line.strip() for line in f if line.strip()]
    return urls


# initialize queue to put all urls to workers to crawl
async def init_queue(urls):
    queue = asyncio.Queue()
    for url in urls:
        await queue.put(url)
    return queue


# Worker function to crawl urls from queue
async def worker(name, queue, session, semaphore, filename, lock):
    while True:
        try:
            print(f"{name} is waiting for a new job...")
            url = await queue.get()
            print(f"{name} started processing: {url}")
        except asyncio.QueueEmpty:
            print(f"{name} found no more jobs and is exiting.")
            break  # Nothing left, exit loop
        
        async with semaphore:  # Rate-limiting
            try:
                async with session.get(url, timeout=10) as response:
                    html = await response.text(errors="ignore")
                    soup = BeautifulSoup(html, "html.parser")
                    title = soup.title.string.strip() if soup.title and soup.title.string else 'No title found'
                    print(f"{name} finished processing: {url} | Title: {title}")
                    async with lock:
                        write_to_file(filename, f"[{name}] {url}: {title}\n")
            except asyncio.TimeoutError:
                print(f"[{name}] Timeout fetching {url}")
            except Exception as e:
                print(f"[{name}] Error fetching {url}: {e}")
            finally:
                queue.task_done()


# Utility function to create output file
def ensure_output_file(filename):
    path = Path(filename)
    if not path.exists():
        path.touch()

# Main function to run the crawler
async def main():
    urls = load_urls()
    queue = await init_queue(urls)
    semaphore = asyncio.Semaphore(5)
    lock = asyncio.Lock()  # Create a lock for file writing

    filename="output.txt"
    ensure_output_file(filename)
    
    async with aiohttp.ClientSession() as session:
        workers = [
            asyncio.create_task(worker(f"Worker-{i+1}", queue, session, semaphore, filename, lock))
            for i in range(5)  # Number of workers = concurrency
        ]
        await queue.join()
        for w in workers:
            w.cancel()  # To make sure workers exit if they're waiting on empty queue

if __name__ == "__main__":
    asyncio.run(main())

