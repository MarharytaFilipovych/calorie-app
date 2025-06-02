from application import app
from flask import request, jsonify, g
from opentelemetry import trace
from logutils import setup_logging
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import SimpleSpanProcessor, ConsoleSpanExporter
from opentelemetry.propagate import extract
from opentelemetry.trace import use_span
from structlog import get_logger

trace.set_tracer_provider(TracerProvider())

setup_logging()
logger = get_logger()

@app.before_request
def start_trace():
    # Extract context from incoming headers
    context = extract(request.headers)
    
    # Start a span within the extracted context
    span_name = f"{request.method} {request.path}"
    tracer = trace.get_tracer(__name__)
    span = tracer.start_span(span_name, context=context)
    
    # Store span in Flask's request context
    g.otel_span = span
    
    # Activate the span in current context (so structlog can pick it up)
    g.otel_cm = use_span(span, end_on_exit=True)
    g.otel_cm.__enter__()

@app.teardown_request
def end_trace(exception=None):
    cm = getattr(g, 'otel_cm', None)
    if cm:
        cm.__exit__(None, None, None)
        
@app.errorhandler(Exception)
def handle_exception(e):
    logger.error("Unhandled exception", exc_info=True)
    return jsonify({"error": str(e)}), 500

#curl -H "traceparent: 00-1234567890abcdef1234567890abcdef-abcdefabcdefabcd-01" http://localhost:5002/hello

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5002)