import logging
import structlog
from opentelemetry import trace

def add_open_telemetry_spans(_, __, event_dict):
    span = trace.get_current_span()
    if not span.is_recording():
        event_dict["span"] = None
        return event_dict

    ctx = span.get_span_context()
    parent = getattr(span, "parent", None)

    event_dict["span"] = {
        "span_id": format(ctx.span_id, "016x"),
        "trace_id": format(ctx.trace_id, "032x"),
        "parent_span_id": None if not parent else format(parent.span_id, "016x"),
    }

    return event_dict

def setup_logging():
    structlog.configure(
        processors=[
	    	# Add log level information
            structlog.stdlib.add_log_level,  
            # додаємо інфу по спанах. має бути перед рендерером
            add_open_telemetry_spans,
            structlog.processors.format_exc_info,
            # Add timestamp in ISO format
            structlog.processors.TimeStamper(fmt="iso"),  
            # Render log entries as JSON
            structlog.processors.JSONRenderer()
        ],
        # Use a dictionary as the context class
        context_class=dict,  
        # Use structlog's logger factory
        logger_factory=structlog.stdlib.LoggerFactory(),  
        # Use structlog's BoundLogger for logger wrapping
        wrapper_class=structlog.stdlib.BoundLogger,  
        # Cache the logger on first use for efficiency
        cache_logger_on_first_use=True,  
    )

    # початок ШІ-згенерованого стрьомногого коду для перенаправлення логів у JSON формат
    formatter = structlog.stdlib.ProcessorFormatter(
        processor=structlog.processors.JSONRenderer(),
        foreign_pre_chain=[
            structlog.stdlib.add_log_level,
            add_open_telemetry_spans,
            structlog.processors.format_exc_info,
            structlog.processors.TimeStamper(fmt="iso")
        ],
    )

    handler = logging.StreamHandler()
    handler.setFormatter(formatter)
    root_logger = logging.getLogger()
    root_logger.handlers = [handler]
    root_logger.setLevel(logging.INFO)
    # кінець ШІ-згенерованого стрьомногого коду для перенаправлення логів у JSON формат