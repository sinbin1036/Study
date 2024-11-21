import React, {useState, useRef} from "react";
import Header from "./Header";
import TodoEditor from "./TodoEditor";
import TodoList from "./TodoList";
import "./App.css"

function App(){
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

  return(
    <div className="App">
      <Header></Header>
      <TodoEditor onCreate={onCreate}></TodoEditor>
      <TodoList todo={todo}></TodoList>
    </div>
  )
}
export default App;