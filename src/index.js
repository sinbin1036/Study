import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './proj02_102/App_page';
import { BrowserRouter } from 'react-router-dom';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <BrowserRouter>
    <App></App>
  </BrowserRouter>
);