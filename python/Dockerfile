FROM python:3.12.3

# Install dependencies for confluent-kafka
RUN apt-get update && apt-get install -y \
    librdkafka-dev \
    && rm -rf /var/lib/apt/lists/*

# Install Python packages
RUN pip install requests confluent-kafka

WORKDIR /app

