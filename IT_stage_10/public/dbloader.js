function loadBase(){
    var dbName = document.getElementById("baseName").value;
    var base_url = "http://localhost:8080/dbs/" + dbName;
    $.ajax({
              url: base_url
            }).then(function(data) {
               $('.data-base').html(data);
            });
}

async function createBase(){
    let url = new URL("http://localhost:8080/dbs");
    url.searchParams.set('dbName', document.getElementById("newBaseName").value)
    console.log(url.href)
    const response = await fetch(url.href, {
        method: 'POST', // *GET, POST, PUT, DELETE, etc.
        mode: 'cors', // no-cors, *cors, same-origin
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        credentials: 'same-origin', // include, *same-origin, omit
        headers: {
          'Content-Type': 'text/xml'
          // 'Content-Type': 'application/x-www-form-urlencoded',
        },
        redirect: 'follow', // manual, *follow, error
        referrerPolicy: 'no-referrer'
      });
    console.log(response.body);
}

async function removeDuplicates(){
    var dbName = document.getElementById("targetBaseName").value;
    var tableName = document.getElementById("targetTableName").value;
    var table_url = "http://localhost:8080/dbs/" + dbName + "/" + tableName + "/rmdp";
    const response = await fetch(table_url, {
            method: 'PUT', // *GET, POST, PUT, DELETE, etc.
            mode: 'cors', // no-cors, *cors, same-origin
            cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
            credentials: 'same-origin', // include, *same-origin, omit
            headers: {
              'Content-Type': 'text/xml'
              // 'Content-Type': 'application/x-www-form-urlencoded',
            },
            redirect: 'follow', // manual, *follow, error
            referrerPolicy: 'no-referrer'
          });
    document.getElementById('baseName').value = dbName;
    loadBase();
}