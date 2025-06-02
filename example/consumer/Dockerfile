FROM python:3.11-slim-bookworm

COPY requirements.txt requirements.txt

RUN pip install --no-cache-dir -r requirements.txt

ADD . .
ENTRYPOINT [ "python" ] 
CMD ["-u", "consumer.py"]
