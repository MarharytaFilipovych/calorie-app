import uuid
from dataclasses import dataclass

@dataclass
class Answer:
    id: uuid.UUID
    author_id: uuid.UUID
    question_id: uuid.UUID
    score: int
    body: str
