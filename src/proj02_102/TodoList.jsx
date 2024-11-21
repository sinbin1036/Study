import React,{useState} from "react";
import TodoItem from "./TodoItem";
import './TodoList.css'

function TodoList({todo}){
  const [search,setSearch] = useState("");

  function onChangeSearch(e) {
    setSearch(e.target.value);
  }
  function getSearchResult() {
    return search === "" //search에 공백일 경우
    ? todo // 참일시 그냥 todo 리턴
    : todo.filter((item)=>item.content.includes(search)); //search에 무언갈 적을시 필터링 발동
  }

  return(
    <div className="TodoList">
      <h3>Todo List</h3>
      <input 
        className="serch_bar" 
        value={search}
        onChange={onChangeSearch}
        placeholder="검색어를 입력하세요"
      />
      <div>
          {getSearchResult().map((item)=> (
          <TodoItem key={item.id} {...item}></TodoItem>
        ))}  
      </div>
    </div>
  )
}
export default TodoList;