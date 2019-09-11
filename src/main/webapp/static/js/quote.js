$(document).ready(function () {
    getQuote();
});

function getQuote() {
    fetch('/api/quote')
        .then(response => {
            return response.json().then(data => {
                if (response.ok) {
                    return data;
                } else {
                    return Promise.reject({status: response.status, data});
                }
            })
        })
        .then(quote => {
            $('#quote-content').html(quote.content);
            $('#quote-author').html(`- ${quote.title} -`);

            $('.quote-wrapper').removeClass('invisible');
        })
        .catch(error => {
            $('.quote-wrapper').removeClass('invisible');
        });
}