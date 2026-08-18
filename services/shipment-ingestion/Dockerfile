FROM golang:1.26-alpine AS build
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -o shipment-ingestion .

FROM alpine:3.24.1
WORKDIR /app
COPY --from=build /app/shipment-ingestion .
EXPOSE 8090
EXPOSE 8091/udp
EXPOSE 8092
CMD ["./shipment-ingestion"]