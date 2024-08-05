## Link

``` http
http://localhost:8070/kibana/app/discover#/view/1e29f380-52ec-11ef-a84c-e7473a05c4fe?_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-15m,to:now))&_a=(columns:!(application,level,message,mdc.traceId,logger),filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:a83d55b0-52a8-11ef-b540-4feb40862eb1,key:host,negate:!t,params:(query:eureka-server.microservices_internal),type:phrase),query:(match_phrase:(host:eureka-server.microservices_internal))),('$state':(store:appState),meta:(alias:!n,disabled:!f,index:a83d55b0-52a8-11ef-b540-4feb40862eb1,key:host,negate:!t,params:(query:gateway-svc.microservices_internal),type:phrase),query:(match_phrase:(host:gateway-svc.microservices_internal))),('$state':(store:appState),meta:(alias:!n,disabled:!f,index:a83d55b0-52a8-11ef-b540-4feb40862eb1,key:message,negate:!t,params:(query:'Resolving%20eureka%20endpoints%20via%20configuration'),type:phrase),query:(match_phrase:(message:'Resolving%20eureka%20endpoints%20via%20configuration')))),grid:(),hideChart:!f,index:a83d55b0-52a8-11ef-b540-4feb40862eb1,interval:auto,query:(language:kuery,query:''),sort:!(!('@timestamp',desc)))
```

## Backup settings

```shell
curl -X POST "http://localhost:5601/kibana/api/saved_objects/_export" -H "kbn-xsrf: true" -H "Content-Type: application/json" -d '{
  "type": ["dashboard", "visualization", "index-pattern", "search"],
  "includeReferencesDeep": true
}' -o kibana-settings.ndjson
```

```shell
curl -X POST "http://localhost:5601/kibana/api/saved_objects/_export" -H "kbn-xsrf: true" -H "Content-Type: application/json" -d '{
  "type": ["dashboard", "visualization", "index-pattern", "search"],
  "includeReferencesDeep": true
}' -o config/kibana/kibana-settings.ndjson
```

## Restore settings

```shell
curl -X POST "http://localhost:5601/kibana/api/saved_objects/_import" -H "kbn-xsrf: true" --form file=@kibana-settings.ndjson
```

```shell
curl -X POST "http://localhost:5601/kibana/api/saved_objects/_import" -H "kbn-xsrf: true" --form file=@config/kibana/kibana-settings.ndjson
```