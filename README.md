# jLedgerClient v1.05

## Step 1

1. Add the JitPack repository to your build file <br>

```
  <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
  </repositories>
  ``` 
  
2.  Add the dependency

```	
   <dependency>
	    <groupId>com.github.ascatox</groupId>
	    <artifactId>jLedgerClient</artifactId>
	    <version>1.0.5</version>
   </dependency>
  ``` 

## Step 2
### HLFLedgerClient

In your project crate a class that `extends` HLFLedgerClient. <br>
Instantiated a `new HLFLedgerClient` <br>

`HLFLedgerClient hlfledgerclient = new HFLedgerClient`

Now you can view a 3 meeethod to working with chaincode: <br>
  1. ```doInvoke(String fcn, List<String> args)```
  2. ```doQuery(String fcn, List<String> args)``` 
  3. ```doRegisterEvent(String eventName, ChaincodeEventListener chaincodeEventListener)```
  
### doInvoke
The Invoke method is invoked whenever the state of the blockchain is queried or modified. <br>
```
public String doInvoke(String fcn, List<String> args) throws JLedgerClientException {
        final InvokeReturn invokeReturn = ledgerInteractionHelper.invokeChaincode(fcn, args);
        try {
            log.debug("BEFORE -> Store Completable Future at " + System.currentTimeMillis());
            invokeReturn.getCompletableFuture().get(configManager.getConfiguration().getTimeout(), TimeUnit.MILLISECONDS);
            log.debug("AFTER -> Store Completable Future at " + System.currentTimeMillis());
            final String payload = invokeReturn.getPayload();
            return payload;
        } catch (Exception e) {
            log.error(fcn.toUpperCase() + " " + e.getMessage());
            throw new JLedgerClientException(fcn + " " + e.getMessage());
        }
    }
   ```
   
This method needs **two** arguments, the **first** is the name of the function we want to invoke in the chaincode. The **second** is the list of args that we want to pass to the chaincode function. <br>
**doInvoke** returns a string. <br>
**Example** <br>
`final String payload = hlfledgerclient.doInvoke(fcn , args);` <br>


### doQuery
A chaincode query is somewhat simpler to implement as it involves the entire network, but simply requires communication from client to peer. <br>


```
public List<String> doQuery(String fcn, List<String> args) throws JLedgerClientException {
        List<String> data = new ArrayList<>();
        try {
            final List<QueryReturn> queryReturns = ledgerInteractionHelper.queryChainCode(fcn, args, null);
            for (QueryReturn queryReturn : queryReturns) {
                data.add(queryReturn.getPayload());
            }
            return data;
        } catch (Exception e) {
            log.error(fcn + " " + e.getMessage());
            throw new JLedgerClientException(fcn + " " + e.getMessage());
        }
  ```
    
    
It is the same seen just before.<br>
**Example** <br>
`final String payload = hlfledgerclient.doQuery(fcn , args);` <br>

### doRegisterEvent

```
 public String doRegisterEvent(String eventName, ChaincodeEventListener chaincodeEventListener) throws JLedgerClientException {
        return ledgerInteractionHelper.getEventHandler().register(eventName, chaincodeEventListener);
    }
 ```
 
 To record an event sent by the chaincode you need a **ChaincodeEventListener** . <br>
 
 ``` ChaincodeEventListener chaincodeEventListener = new ChaincodeEventListener() {
                @Override
                public void received(String handle, BlockEvent blockEvent, ChaincodeEvent chaincodeEvent) {
                    String payload = new String(chaincodeEvent.getPayload());
                    System.out.println("Event from chaincode: " + chaincodeEvent.getEventName() + " " + payload);

                }
            };
```
Now you can call **doRegisterEvent** <br>

**Example** <br>

`ledgerClient.doRegisterEvent("EVENT", chaincodeEventListener);` <br>

**Watch out!!!** The string *eventName* must be the same as the event created in the chaincode!!!
 
 


    

 

