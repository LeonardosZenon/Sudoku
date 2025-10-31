# SUDOKU

---

<!-- TOC -->
* [SUDOKU](#sudoku)
  * [DATABASE](#database)
  * [DOCKER](#docker)
<!-- TOC -->

---

## DATABASE
**provide your own postgres**

* To setup another database and schena to connect to, change the application.properties


---

## DOCKER

* To kill the image you need to exec into the running image and then kill the running processes:
  * ```bash
    ps aux
    ```
  * ```bash
    kill -9 <PID-SpringBoot> <PID-KeyCloak>
    ```