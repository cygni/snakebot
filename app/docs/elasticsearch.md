# Using Elasticsearch
Elasticsearch is used (for the production profile) for storing all game events, 
game results and client infos. If starting from scratch it is important that 
the mappings are defined correctly. The mappings define the data type for
each field and if there are any special index options.

Generally for String types we have choosen the index: "not_analyzed". This
means that words will not be split.

There are three indexes in Elasticsearch: clientinfo, gamehistory and 
gameevent.

Since it is not possible to change the mapping after it has been created
it is important to define this before the server starts storing data. 

## Setting the index and mapping

Get an interactive shell to the docker container for elasticsearch:
```bash
> docker exec -it elasticsearch /bin/bash
```

Delete any existing index and mapping (Note! this will delete all 
existing documents in this index):
```bash
> curl -XDELETE localhost:9200/clientinfo
```

Push the new index and mapping:
```bash
> curl -XPUT 'localhost:9200/clientinfo' -d'
  {
    "mappings": {
      "client": {
        "properties": {
          "clientVersion": {
            "type": "string",
            "index": "not_analyzed"
          },
          "ipAddress": {
            "type": "ip",
            "index": "not_analyzed"
          },
          "language": {
            "type": "string",
            "index": "not_analyzed"
          },
          "operatingSystem": {
            "type": "string",
            "index": "not_analyzed"
          },
          "receivingPlayerId": {
            "type": "string",
            "index": "not_analyzed"
          },
          "type": {
            "type": "string",
            "index": "not_analyzed"
          }
        }
      }
    }
  }'
```

Do this for all indexes. The current mappings can be found in the 
[resources](https://github.com/cygni/snakebot/tree/master/app/src/main/resources)
directory for the project. 