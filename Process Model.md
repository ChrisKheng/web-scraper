Program starts -> parse inputs -> spawn App thread -> set timer -> wait for timer to run down

App starts -> initialise -> process Seed.txt -> spawn Crawler, Builder -> start all threads 
    -> wait for Crawlers to finish  [] wait to be interrupted 

If App is interrupted -> interrupt all threads -> wait for all threads to finish -> terminates 

Program terminates -> Cleaner starts -> write remaining urls into disk -> computer statistics -> shut down

Potential Deadlock: usage of semaphores
-> Prove this doesn't cause deadlock

Crawler() = if not interrupted -> getUrl from queue -> visit page -> extract Urls -> check if Urls are in IUT -> add Urls to queue if 
    not duplicated -> attempt to grab crawler sempahore -> add Url and page to buffer -> release builder sempahore
    -> Crawler

Builder() = attempt to grab builder semaphore -> write all Urls into IUT -> release crawler semaphore -> Builder
-> Stop if interrupt exception is thrown

Cleaner() = write all remaining urls from buffer to IUT -> write all remaining urls from queue to disk -> report statistics
    -> ✓