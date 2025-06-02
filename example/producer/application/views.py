from application import app
from flask import request, jsonify
from sender_service import send_message

import structlog

import uuid

logger = structlog.get_logger()

@app.route('/hello')
def home():
    logger.info("Received a request to /hello")
    logger.info("second log, same span!")
    return "Hello, World!"

@app.route('/messages', methods=['POST'])
def example():
    logger.info("Received a new message request")
    # це приклад, івен-сорсинг в вас має бути з сервісного шару, а не в контролері
    print("new message received")
    task = init_task(request.get_json())
    send_message(task)
    return jsonify(task), 201

def init_task(data):
    task = {};
    task["message"] = data["message"]
    task["id"] = str(uuid.uuid4()) 
    return task

