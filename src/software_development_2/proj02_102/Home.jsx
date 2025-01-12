import React, {useState, useRef} from "react";
import Header from "./Header";
import TodoEditor from "./TodoEditor";
import TodoList from "./TodoList";
import "./App.css"

function Home(){
  const [todo, setTodo] = useState([]);
  const idRef = useRef(0);

  function onCreate(content) {
    const newItem = {
      id: idRef.current,
      isDone: false,
      content,
      createdDate: new Date().getTime(),
    };

    setTodo([newItem, ...todo]);
    idRef.current += 1;
  }
  
  function onUpdate(targetId) {
    setTodo(todo.map((item)=>
      item.id === targetId
    ? {...item,isDone: !item.isDone}
    : item)
    )
  }

  function onDelete(targetId) {
    setTodo(todo.filter((item)=>item.id !== targetId))
  }

  return(
    <div className="App">
      <Header></Header>
      <TodoEditor onCreate={onCreate}></TodoEditor>
      <TodoList todo={todo} onUpdate={onUpdate} onDelete={onDelete}></TodoList>
    </div>
  )
}
export default Home;  