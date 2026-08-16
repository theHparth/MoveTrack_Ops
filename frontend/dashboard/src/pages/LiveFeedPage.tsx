import useLiveFeed from "../hooks/useLiveFeed";

function LiveFeedPage() {
  const pings = useLiveFeed("ws://localhost:8095");

  return (
    <section>
      <h1>Live Shipment Feed</h1>
      <ul>
        {pings.map((ping, i) => (
          <li key={i}>
            {ping.device_id} — {ping.latitude}, {ping.longitude} @{" "}
            {ping.timestamp}
          </li>
        ))}
      </ul>
    </section>
  );
}

export default LiveFeedPage;
