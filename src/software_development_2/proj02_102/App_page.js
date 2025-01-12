import { Routes, Route} from "react-router-dom";
import Home from "./Home";
import Todo from "./pages/Todo";


function App() {
    return(
        <div>
            <div className="App">
                <Routes>
                    <Route path="/" element={<Home></Home>}></Route>
                    <Route path="/todo/:id" element={<Todo></Todo>}></Route>
                </Routes>
            </div>
        </div>
    );
};

export default App;