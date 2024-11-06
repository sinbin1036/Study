import React, { useState, useEffect } from 'react';
import config from './apikey';

const Youtube_Api_key = config.Youtube_Api_key;

const App = () => {
  const [videos, setVideos] = useState([]);
  const [loading, setLoading] = useState(true);

  // 유튜브 API 호출 함수
  const fetchVideos = async () => {
    try {
      const response = await fetch(
        `https://www.googleapis.com/youtube/v3/search?part=snippet&q=노래&key=${Youtube_Api_key}`
      );
      const data = await response.json();
      setVideos(data.items);  // API로부터 받은 데이터를 상태에 저장
      setLoading(false);
    } catch (error) {
      console.error("API 호출 에러:", error);
      setLoading(false);
    }
  };

  // 컴포넌트가 마운트될 때 유튜브 API 호출
  useEffect(() => {
    fetchVideos();
  }, []);

  return (
    <div>
      <h1>My YouTube Playlist</h1>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <ul>
          {videos.map((video) => (
            <li key={video.id.videoId}>
              <h3>{video.snippet.title}</h3>
              <img
                src={video.snippet.thumbnails.default.url}
                alt={video.snippet.title}
              />
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default App;
