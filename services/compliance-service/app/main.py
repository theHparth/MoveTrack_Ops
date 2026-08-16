from fastapi import FastAPI

app = FastAPI(title="Compliance Service")


@app.get("/health")
def health():
    return {"status": "ok"}