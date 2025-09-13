function ajax(verb, url, data, fulfilled, rejected) {
    return new Promise(function (fulfilled, rejected) {
        // Create XMLHttpRequest object
        let req = new XMLHttpRequest();

        // When the entire request fails it is probably a network error
        req.onerror = function () {
            rejected(new Error('There was a network error.'));
        };

        // Setup state change event
        req.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE) {
                // Check status property to see what is going on
                if (this.status >= 200 && this.status < 400) {
                    fulfilled(this);
                } else if (this.status >= 400) {
                    rejected({
                        "status": this.status,
                        "statusText": this.statusText,
                        "response": this.response,
                        "responseText": this.responseText
                    });
                }
            }
        };

        // Open Request
        req.open(verb, url);

        // Set headers for JSON
        req.setRequestHeader("Content-Type", "application/json");

        // Check to see if we need to pass data
        if (data) {
            // Submit the request with data
            req.send(JSON.stringify(data));
        } else {
            // Submit the request
            req.send();
        }
    });
}