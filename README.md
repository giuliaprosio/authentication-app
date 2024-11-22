# Authentication Frontend Application

This project is a frontend for an authorization and authentication 
application built with **React** and **Vite**. 
React serves as the framework for building the UI, while Vite is 
used for fast building and efficient development. 
The app uses `axios` to manage HTTP requests to the backend server. 

## Dependencies
These are the core libraries that you will need: 
```
npm install axios react react-dom react-router-dom bootstrap
```

Vite needs some additional packages for a complete setup, especially 
to handle JSX and other React-specific configutations. These 
packages are typically automatically added when initializing a project with Vite's React template, but here they are explicitly: 
```
npm install --save-dev vite @vitejs/plugin-react
```
## Available Scripts

In the project directory, you can run:

### `npm run dev`

Runs the app in the development mode.\
Open [http://localhost:5173](http://localhost:5173) to view it in your browser.

### `npm run build`

Builds the app for production, creating an optimized bundle in the `dist` folder.\
The production build is optimized for performance and ready for 
deployment. 

### `npm run serve`
After building, you have two options for serving the app: 
1. **Serve directly from the backend server**: copy the contents of the `dist` folder to the `./resources` directory of the backend app. 
2. **Serve with a separate static server**: in a two-tier architecture (preferred by React), the frontend is served separatedly from the backend. 
To serve the frontend separatedly, a possible solution I offer is: 
    1. Install `serve` if you haven't already: 

        ``` 
        npm install -g serve 
        ```

    2. Run the `npm run serve` command (making sure you have built the project in the `dist` folder). 
The application will be accessible at [http://localhost:3000](http://localhost:3000). 

## Run with Docker 
From the terminal in the same directory as the 
```Dockerfile``` run 
```
 docker build -t userapp-ui .
 docker run -p 3000 userapp-ui
```