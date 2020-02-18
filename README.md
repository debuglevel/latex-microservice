# LaTeX Microservice
This is a simple REST microservice to compile LaTeX documents.

# HTTP API
Documents have to be POSTed first to the microservice before you can GET them.
 
## Add document
To POST a document, create a JSON file which contains all necessary files. The files are described by their name (may contain a directory) and their Base64 encoded data.

We will upload two files:
```
$ cat main.tex
\documentclass{article}
\begin{document}
\include{test/test}
\end{document}

$ cat test/test.tex
hallo welt
```

Encoded with Base64, they look like this:
```
$ base64 -w0 main.tex
XGRvY3VtZW50Y2xhc3N7YXJ0aWNsZX0KXGJlZ2lue2RvY3VtZW50fQpcaW5jbHVkZXt0ZXN0L3Rlc3R9ClxlbmR7ZG9jdW1lbnR9

$ base64 -w0 test/test.tex
aGFsbG8gd2VsdA==
```

We place their Base64 encoded content in a JSON (Base64 can just be used in JSON without doing any harm and no need to escape). We can also define packages (e.g. "12many") to be installed explicitly, if MikTex is unable to download them automatically.
```
$ cat upload.json
{
  "requiredPackages": [ "12many" ],
  "files": [
     {
        "name": "main.tex",
        "base64data": "XGRvY3VtZW50Y2xhc3N7YXJ0aWNsZX0KXGJlZ2lue2RvY3VtZW50fQpcaW5jbHVkZXt0ZXN0L3Rlc3R9ClxlbmR7ZG9jdW1lbnR9"
     },
     {
        "name": "test/test.tex",
        "base64data": "aGFsbG8gd2VsdA=="
     }
  ]
}
```

Then we upload this JSON to the microservice. The POST request returns an unique ID which identifies the document.
```
$ curl -X POST -d @upload.json -H "Content-Type: application/json" -H "Accept: application/json" http://localhost/documents/
2b5abff3-cf98-4837-b579-25bda3343bb9
```

## Get compiled PDF documents
When you GET the document, you receive a JSON containing some information about the executed LaTeX process and all generated files. Those are again encoded in Base64.
```
$ curl -X GET -H "Content-Type: application/json" -H "Accept: application/json" http://localhost/documents/2b5abff3-cf98-4837-b579-25bda3343bb9
{
  "success": true,
  "exitValue": 0,
  "durationMilliseconds": 17598,
  "files": [
    {
      "name": "output/test/test.aux",
      "base64data": "XHJlbGF4IApcQHNldGNrcHR7dGVzdC90ZXN0fXsKXHNldGNvdW50ZXJ7cGFnZX17Mn0KXHNldGNvdW50ZXJ7ZXF1YXRpb259ezB9ClxzZXRjb3VudGVye2VudW1pfXswfQpcc2V0Y291bnRlcntlbnVtaWl9ezB9ClxzZXRjb3VudGVye2VudW1paWl9ezB9ClxzZXRjb3VudGVye2VudW1pdn17MH0KXHNldGNvdW50ZXJ7Zm9vdG5vdGV9ezB9ClxzZXRjb3VudGVye21wZm9vdG5vdGV9ezB9ClxzZXRjb3VudGVye3BhcnR9ezB9ClxzZXRjb3VudGVye3NlY3Rpb259ezB9ClxzZXRjb3VudGVye3N1YnNlY3Rpb259ezB9ClxzZXRjb3VudGVye3N1YnN1YnNlY3Rpb259ezB9ClxzZXRjb3VudGVye3BhcmFncmFwaH17MH0KXHNldGNvdW50ZXJ7c3VicGFyYWdyYXBofXswfQpcc2V0Y291bnRlcntmaWd1cmV9ezB9ClxzZXRjb3VudGVye3RhYmxlfXswfQp9Cg=="
    },
    {
      "name": "output/main.aux",
      "base64data": "XHJlbGF4IApcQGlucHV0e3Rlc3QvdGVzdC5hdXh9Cg=="
    },
    {
      "name": "output/main.pdf",
      "base64data": "JVBERi0xLjUKJdDUxdgKMyAwIG9iago8PAovTGVuZ3RoIDk0ICAgICAgICAKL0ZpbHRlciAvRmxhdGVEZWNvZGUKPj4Kc3RyZWFtCnjacwrh0nezULDUszQzMlMISVMwNLHQMzc0UjA3MNczNDJVCElRiNbISMzJydfUNTY21ijXNDLXSM0p0YwN8VIwNDXRMzEyVNA1NTPXszCHKDYESXG5hnABANUNFVMKZW5kc3RyZWFtCmVuZG9iagoyIDAgb2JqCjw8Ci9UeXBlIC9QYWdlCi9Db250ZW50cyAzIDAgUgovUmVzb3VyY2VzIDEgMCBSCi9NZWRpYUJveCBbMCAwIDU5NS4yNzYgODQxLjg5XQovUGFyZW50IDUgMCBSCj4+CmVuZG9iagoxIDAgb2JqCjw8Ci9Gb250IDw8IC9GOCA0IDAgUiA+PgovUHJvY1NldCBbIC9QREYgL1RleHQgXQo+PgplbmRvYmoKNiAwIG9iagpbNTAwIDUwMCA1MDAgNTAwIDUwMCA1MDAgNTAwIDUwMCA1MDAgMjc3LjggMjc3LjggMjc3LjggNzc3LjggNDcyLjIgNDcyLjIgNzc3LjggNzUwIDcwOC4zIDcyMi4yIDc2My45IDY4MC42IDY1Mi44IDc4NC43IDc1MCAzNjEuMSA1MTMuOSA3NzcuOCA2MjUgOTE2LjcgNzUwIDc3Ny44IDY4MC42IDc3Ny44IDczNi4xIDU1NS42IDcyMi4yIDc1MCA3NTAgMTAyNy44IDc1MCA3NTAgNjExLjEgMjc3LjggNTAwIDI3Ny44IDUwMCAyNzcuOCAyNzcuOCA1MDAgNTU1LjYgNDQ0LjQgNTU1LjYgNDQ0LjQgMzA1LjYgNTAwIDU1NS42IDI3Ny44IDMwNS42IDUyNy44IDI3Ny44IDgzMy4zIDU1NS42IDUwMCA1NTUuNiA1MjcuOCAzOTEuNyAzOTQuNCAzODguOSA1NTUuNiA1MjcuOCA3MjIuMl0KZW5kb2JqCjcgMCBvYmoKPDwKL0xlbmd0aDEgMTQ2OQovTGVuZ3RoMiA4NTY0Ci9MZW5ndGgzIDAKL0xlbmd0aCA5NTUyICAgICAgCi9GaWx0ZXIgL0ZsYXRlRGVjb2RlCj4+CnN0cmVhbQp42o20BVSU3Ro2TKd0IzF0SXf30CXdDDDAIMwQQ4OApISU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b3VudCAxCi9LaWRzIFsyIDAgUl0KPj4KZW5kb2JqCjkgMCBvYmoKPDwKL1R5cGUgL0NhdGFsb2cKL1BhZ2VzIDUgMCBSCj4+CmVuZG9iagoxMCAwIG9iago8PAovUHJvZHVjZXIgKE1pS1RlWCBwZGZUZVgtMS40MC4yMCkKL0NyZWF0b3IgKFRlWCkKL0NyZWF0aW9uRGF0ZSAoRDoyMDE5MDMwODE1NDAwN1opCi9Nb2REYXRlIChEOjIwMTkwMzA4MTU0MDA3WikKL1RyYXBwZWQgL0ZhbHNlCi9QVEVYLkZ1bGxiYW5uZXIgKFRoaXMgaXMgTWlLVGVYLXBkZlRlWCAyLjkuNjk1OSAoMS40MC4yMCkpCj4+CmVuZG9iagp4cmVmCjAgMTEKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDAwMjk5IDAwMDAwIG4gCjAwMDAwMDAxODggMDAwMDAgbiAKMDAwMDAwMDAxNSAwMDAwMCBuIAowMDAwMDEwNjM3IDAwMDAwIG4gCjAwMDAwMTA3NzQgMDAwMDAgbiAKMDAwMDAwMDM2NiAwMDAwMCBuIAowMDAwMDAwNzY2IDAwMDAwIG4gCjAwMDAwMTA0MzYgMDAwMDAgbiAKMDAwMDAxMDgzMSAwMDAwMCBuIAowMDAwMDEwODgwIDAwMDAwIG4gCnRyYWlsZXIKPDwgL1NpemUgMTEKL1Jvb3QgOSAwIFIKL0luZm8gMTAgMCBSCi9JRCBbPDdDMDAzODEzOEEwRkIxMEU5NERFOTRCOTRFNTdGQjFCPiA8N0MwMDM4MTM4QTBGQjEwRTk0REU5NEI5NEU1N0ZCMUI+XSA+PgpzdGFydHhyZWYKMTEwOTAKJSVFT0YK"
    },
    {
      "name": "output/main.log",
      "base64data": "VGhpcyBpcyBwZGZUZVgsIFZlcnNpb24gMy4xNDE1OTI2NS0yLjYtMS40MC4yMCAoTWlLVGVYIDIuOS42OTgwKSAocHJlbG9hZGVkIGZvcm1hdD1wZGZsYXRleCAyMDE5LjMuOCkgIDggTUFSIDIwMTkgMTU6NDAKZW50ZXJpbmcgZXh0ZW5kZWQgbW9kZQoqKi4vbWFpbi50ZXgKKG1haW4udGV4CkxhVGVYMmUgPDIwMTgtMTItMDE+CigvbWlrdGV4Ly5taWt0ZXgvdGV4bWZzL2luc3RhbGwvdGV4L2xhdGV4L2Jhc2UvYXJ0aWNsZS5jbHMKRG9jdW1lbnQgQ2xhc3M6IGFydGljbGUgMjAxOC8wOS8wMyB2MS40aSBTdGFuZGFyZCBMYVRlWCBkb2N1bWVudCBjbGFzcwooL21pa3RleC8ubWlrdGV4L3RleG1mcy9pbnN0YWxsL3RleC9sYXRleC9iYXNlL3NpemUxMC5jbG8KRmlsZTogc2l6ZTEwLmNsbyAyMDE4LzA5LzAzIHYxLjRpIFN0YW5kYXJkIExhVGVYIGZpbGUgKHNpemUgb3B0aW9uKQopClxjQHBhcnQ9XGNvdW50ODAKXGNAc2VjdGlvbj1cY291bnQ4MQpcY0BzdWJzZWN0aW9uPVxjb3VudDgyClxjQHN1YnN1YnNlY3Rpb249XGNvdW50ODMKXGNAcGFyYWdyYXBoPVxjb3VudDg0ClxjQHN1YnBhcmFncmFwaD1cY291bnQ4NQpcY0BmaWd1cmU9XGNvdW50ODYKXGNAdGFibGU9XGNvdW50ODcKXGFib3ZlY2FwdGlvbnNraXA9XHNraXA0MQpcYmVsb3djYXB0aW9uc2tpcD1cc2tpcDQyClxiaWJpbmRlbnQ9XGRpbWVuMTAyCikKTm8gZmlsZSBtYWluLmF1eC4KXG9wZW5vdXQxID0gYG1haW4uYXV4Jy4KCkxhVGVYIEZvbnQgSW5mbzogICAgQ2hlY2tpbmcgZGVmYXVsdHMgZm9yIE9NTC9jbW0vbS9pdCBvbiBpbnB1dCBsaW5lIDIuCkxhVGVYIEZvbnQgSW5mbzogICAgLi4uIG9rYXkgb24gaW5wdXQgbGluZSAyLgpMYVRlWCBGb250IEluZm86ICAgIENoZWNraW5nIGRlZmF1bHRzIGZvciBUMS9jbXIvbS9uIG9uIGlucHV0IGxpbmUgMi4KTGFUZVggRm9udCBJbmZvOiAgICAuLi4gb2theSBvbiBpbnB1dCBsaW5lIDIuCkxhVGVYIEZvbnQgSW5mbzogICAgQ2hlY2tpbmcgZGVmYXVsdHMgZm9yIE9UMS9jbXIvbS9uIG9uIGlucHV0IGxpbmUgMi4KTGFUZVggRm9udCBJbmZvOiAgICAuLi4gb2theSBvbiBpbnB1dCBsaW5lIDIuCkxhVGVYIEZvbnQgSW5mbzogICAgQ2hlY2tpbmcgZGVmYXVsdHMgZm9yIE9NUy9jbXN5L20vbiBvbiBpbnB1dCBsaW5lIDIuCkxhVGVYIEZvbnQgSW5mbzogICAgLi4uIG9rYXkgb24gaW5wdXQgbGluZSAyLgpMYVRlWCBGb250IEluZm86ICAgIENoZWNraW5nIGRlZmF1bHRzIGZvciBPTVgvY21leC9tL24gb24gaW5wdXQgbGluZSAyLgpMYVRlWCBGb250IEluZm86ICAgIC4uLiBva2F5IG9uIGlucHV0IGxpbmUgMi4KTGFUZVggRm9udCBJbmZvOiAgICBDaGVja2luZyBkZWZhdWx0cyBmb3IgVS9jbXIvbS9uIG9uIGlucHV0IGxpbmUgMi4KTGFUZVggRm9udCBJbmZvOiAgICAuLi4gb2theSBvbiBpbnB1dCBsaW5lIDIuClxvcGVub3V0MiA9IGB0ZXN0L3Rlc3QuYXV4Jy4KCih0ZXN0L3Rlc3QudGV4KSBbMQoKCnsvbWlrdGV4Ly5taWt0ZXgvdGV4bWZzL2RhdGEvcGRmdGV4L2NvbmZpZy9wZGZ0ZXgubWFwfV0KKC90bXAvbGF0ZXgtbWljcm9zZXJ2aWNlNzY5MzU0OTgzNTY3NzExNTcxNS9vdXRwdXQvbWFpbi5hdXgKKC90bXAvbGF0ZXgtbWljcm9zZXJ2aWNlNzY5MzU0OTgzNTY3NzExNTcxNS9vdXRwdXQvdGVzdC90ZXN0LmF1eCkpICkgCkhlcmUgaXMgaG93IG11Y2ggb2YgVGVYJ3MgbWVtb3J5IHlvdSB1c2VkOgogMjEwIHN0cmluZ3Mgb3V0IG9mIDQ5NDg2NQogMjcwNSBzdHJpbmcgY2hhcmFjdGVycyBvdXQgb2YgMzE3ODU1OAogNTQ0NjUgd29yZHMgb2YgbWVtb3J5IG91dCBvZiAzMDAwMDAwCiAzOTQwIG11bHRpbGV0dGVyIGNvbnRyb2wgc2VxdWVuY2VzIG91dCBvZiAxNTAwMCsyMDAwMDAKIDM2NDAgd29yZHMgb2YgZm9udCBpbmZvIGZvciAxNCBmb250cywgb3V0IG9mIDMwMDAwMDAgZm9yIDkwMDAKIDAgaHlwaGVuYXRpb24gZXhjZXB0aW9ucyBvdXQgb2YgODE5MQogMjNpLDRuLDE3cCwxMzBiLDEwN3Mgc3RhY2sgcG9zaXRpb25zIG91dCBvZiA1MDAwaSw1MDBuLDEwMDAwcCwyMDAwMDBiLDUwMDAwcwo8L3Vzci9sb2NhbAovc2hhcmUvbWlrdGV4LXRleG1mL2ZvbnRzL3R5cGUxL3B1YmxpYy9hbXNmb250cy9jbS9jbXIxMC5wZmI+Ck91dHB1dCB3cml0dGVuIG9uIC90bXAvbGF0ZXgtbWljcm9zZXJ2aWNlNzY5MzU0OTgzNTY3NzExNTcxNS9vdXRwdXQvbWFpbi5wZGYgKDEKIHBhZ2UsIDExNDY2IGJ5dGVzKS4KUERGIHN0YXRpc3RpY3M6CiAxMCBQREYgb2JqZWN0cyBvdXQgb2YgMTAwMCAobWF4LiA4Mzg4NjA3KQogMCBuYW1lZCBkZXN0aW5hdGlvbnMgb3V0IG9mIDEwMDAgKG1heC4gNTAwMDAwKQogMSB3b3JkcyBvZiBleHRyYSBtZW1vcnkgZm9yIFBERiBvdXRwdXQgb3V0IG9mIDEwMDAwIChtYXguIDEwMDAwMDAwKQoK"
    }
  ],
  "output": "This is pdfTeX, Version 3.14159265-2.6-1.40.20 (MiKTeX 2.9.6980)\nentering extended mode\n(main.tex\nLaTeX2e <2018-12-01>\n(/miktex/.miktex/texmfs/install/tex/latex/base/article.cls\nDocument Class: article 2018/09/03 v1.4i Standard LaTeX document class\n(/miktex/.miktex/texmfs/install/tex/latex/base/size10.clo))\nNo file main.aux.\n(test/test.tex) [1{/miktex/.miktex/texmfs/data/pdftex/config/pdftex.map}]\n(/tmp/latex-microservice7693549835677115715/output/main.aux\n(/tmp/latex-microservice7693549835677115715/output/test/test.aux)) )</usr/local\n/share/miktex-texmf/fonts/type1/public/amsfonts/cm/cmr10.pfb>\nOutput written on /tmp/latex-microservice7693549835677115715/output/main.pdf (1\n page, 11466 bytes).\nTranscript written on /tmp/latex-microservice7693549835677115715/output/main.lo\ng.\n"
```

# Security
Although `--shell-escape` or `--enable-write18` are not enabled (which would enable running commands from within a TeX file), TeX is a rather mighty language and files in the filesystem can be read and written. Even if deployed as a docker container, the container still can be compromised during the rest of its lifetime.

# Docker
## Preinstall packages
By setting e.g. `INSTALL_PACKAGES=12many a2ping` you can install packages at the startup of the container. This is not necessary, as MikTex will download missing packages while document compilation. But it can speed up compilations by shifting the download at the container startup instead of the document compilation. This might be useful, if you already know which packages will be required by the documents.