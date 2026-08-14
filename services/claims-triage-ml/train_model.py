import os
import pandas as pd
from sklearn.compose import ColumnTransformer
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder
import joblib

DATA_PATH = os.path.join("data", "claims_sample.csv")
MODEL_DIR = "model"
MODEL_PATH = os.path.join(MODEL_DIR, "triage_model.joblib")

FEATURE_COLUMNS = ["claim_amount", "days_since_incident", "claim_type", "description_length"]
LABEL_COLUMN = "priority"
CATEGORICAL_COLUMNS = ["claim_type"]
NUMERIC_COLUMNS = ["claim_amount", "days_since_incident", "description_length"]


def build_pipeline():
    preprocessor = ColumnTransformer(
        transformers=[
            ("category", OneHotEncoder(handle_unknown="ignore"), CATEGORICAL_COLUMNS),
        ],
        remainder="passthrough",
    )
    classifier = RandomForestClassifier(n_estimators=100, random_state=42)
    return Pipeline(steps=[("preprocess", preprocessor), ("classify", classifier)])


def main():
    df = pd.read_csv(DATA_PATH)
    X = df[FEATURE_COLUMNS]
    y = df[LABEL_COLUMN]

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.25, random_state=42, stratify=y
    )

    pipeline = build_pipeline()
    pipeline.fit(X_train, y_train)

    predictions = pipeline.predict(X_test)
    accuracy = accuracy_score(y_test, predictions)
    print(f"Test accuracy: {accuracy:.2f}")

    os.makedirs(MODEL_DIR, exist_ok=True)
    joblib.dump(pipeline, MODEL_PATH)
    print(f"Model saved to {MODEL_PATH}")


if __name__ == "__main__":
    main()