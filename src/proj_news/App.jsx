import React, { useEffect, useState } from "react";
import axios from "axios";

const Search = () => {
  const [data, setData] = useState([]);

  const getSerchDate = async () => {
    const url = "v1/search/news.json";
    const options = {
      // 요청헤더 설정
      headers: {
        "X-Naver-Client-Id": process.env.REACT_APP_NEWS_API_CLIENT_ID,
        "X-Naver-Client-Secret": process.env.REACT_APP_NEWS_API_CLIENT_SECRET,
      },
      // 파라미터 설정
      params: {
        query: "비트코인",
      },
    };

    const response = await axios.get(url, options);
    console.log(response.data);

    setData(response.data.items);
  };

  useEffect(() => {
    getSerchDate();
  }, []);

  return (
    <div>
      {data.map((item, index) => (
        <div ke={index}>
          <h2>{item.title}</h2>
          <hr />
        </div>
      ))}
    </div>
  );
};

export default Search;