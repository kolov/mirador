# mirador

Live reload on file change for Clojure Ring applications

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
    
    
This says it all: you set a watcher to watch:
  - a change in a folder and subfolders
  - a change in a list of files
  - wahtever else you can think of
  
The middleware will schedule the watchers (default time 100 ms) and sent a message to the socket so that the broser can reload. Pretty simple, I wander why did I work without is so long.

## Q & A

### Q The watchers will detect the change ant the browser will reload, 
## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
