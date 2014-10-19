# mirador

Live reload on source changes for Clojure Ring applications

## Usage

add to dependencies:

    [com.akolov.mirador "0.1.0"]
    
    
and then to your ring middleware:

    watch-reload {:watcher (watcher-folder "resources")
                            :uri     "/watch-reload"})
                            
and then a piece of Javascript to your page :

    <script language="JavaScript">
        socket= new WebSocket('ws://localhost:3001/watch-reload');
        socket.onopen= function() {
            socket.send('watch');
        };
        socket.onmessage= function(s) {
            if( s.data == 'started') {
                console.log("Watching started");
            } else if( s.data == 'reload') {
                console.log("reloading");
                window.location.reload();
            } else {
                alert('Don\'t know what to do with [' + s.data + ']');
            }
        };
    </script>
    
    
That's it: you set a watcher to watch:
  - a change in a folder and subfolders
  - a change in a list of files
  - wahtever else you can think of
  
The middleware will schedule the watchers (default time 100 ms) and send a message to the socket if any watcher fires, the code listening in the browser will reload the page. Pretty simple, I wander why did I work so long without it.

## Q & A

##### What is this needed for?
Mostly static content, css and enlive templates, I think. Clojurscript seem to have good support for live reload, and code changes usually need more sofosticated scenarios than just reloading.
##### The watchers will detect the change ant the browser will reload, but how will the reloaded content be up to date?
It is up to other middleware to update before processing the request - ring.middleware.reload/wrap-reload will relaod any updated namespaces, com.akolov.enlive-reload/wrap-enlive-reload will reload any affected enlive templates.
##### Why Javascript and not Clojurescript?
Feel free to rewrite the client code snippet to fit your enironment/process.


## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
