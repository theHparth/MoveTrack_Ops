import { useEffect, useState } from "react";

function useLiveFeed(url: string) {
  const [pings, setPings] = useState<any[]>([]);

  useEffect(() => {
    const socket = new WebSocket(url);

    socket.onmessage = (event) => {
      const ping = JSON.parse(event.data);
      setPings((prev) => [ping, ...prev].slice(0, 50));
    };

    return () => socket.close();
  }, [url]);

  return pings;
}

export default useLiveFeed;
