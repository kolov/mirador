# mirador
## If anything changes, we'll tell the browser. Immediately.

Live reload on changes in static files and Enlive templates for Clojure Ring applications

## Usage

Add to dependencies:
```clojure
    [com.akolov.mirador "0.1.0"]
```    
    
Add a ring middleware to watch for changes. For example, react when an enlive template or snippet has changed::
```clojure
    watch-reload {:watcher (watcher-folder "resources/pages")
                            :uri     "/watch-reload"})
```                            
and then a piece of Javascript to your page :
```javascript

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

```   
    
You set a predifend watcher or implement your own. The middleware will schedule the watchers (default time 100 ms) and send a message to the socket if any watcher fires, the code listening in the browser will reload the page. Pretty simple, I wander why did I work so long without it.

## Demo

See the [demo project] (https://github.com/kolov/demo-mirador) and the demo: ![video](https://github.com/kolov/mirador/blob/develop/doc/video.gif)

## Q & A

##### What is this needed for?
Mostly static content, enlive templates, css and Javascript, I think. Clojurscript seem to have good support for live reload, and code changes usually need more sofisticated scenarios than just reloading.
##### The watchers will detect the change and the browser will reload, but how will the reloaded content be up to date?
It is up to other middleware to update before processing the request - Code changes will be reloaded by ring.middleware.reload/wrap-reload. To reload any affected enlive templates on the fly, use [com.akolov.enlive-reload/wrap-enlive-reload](https://github.com/kolov/enlive-reload). You could implement a watcher that does some extra work before notifying about the change.
##### Why Javascript and not Clojurescript?
The tiny code snippet needed in the browser can easily be rewritten to anything executable in the browser that fits your enironment/process.
##### How are file updates detected?
The java.nio.File.WatchService alerts extremely slow on Mac OS (haven't checked on others), mirador just checks filetimes few times a second.  


## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
