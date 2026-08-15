FROM golang:1.26-alpine AS build
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -o shipment-ingestion .

FROM alpine:3.20
WORKDIR /app
COPY --from=build /app/shipment-ingestion .
EXPOSE 8090
CMD ["./shipment-ingestion"]