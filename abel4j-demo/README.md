# Understanding the Abelian Java SDK by Demos

> [!NOTE]
> All demos described in this document are run on the testnet of the Abelian blockchain. The testnet is a separate blockchain from the mainnet and is used for testing purposes only. The built-in accounts used in the demos already has some ABELs and transactions on the testnet. As such, you can run the demos without setting up any testnet accounts or sending any ABELs to the built-in accounts.

## 1. Prepare the environment

### 1.1. Get the demos

Download and decompress `abec4j-demo-x.y.z.zip` to get the demo directory `abel4j-demo`. Go to the demo directory and we will run all the demos from there.

```bash
unzip abel4j-demo-x.y.z.zip
cd abel4j-demo
```

### 1.2. Test Java environment

Run the following command to test if your Java environment is ready for the demos.

```bash
bin/abel4j-demo
```

The expected output should be like this:

```text
Usage: java -jar abec4j-demo.jar <demoName> [demoArgs]
Available demos:
  Crypto
  AbecRPCClient
  Account
  WalletDB
  GetSafeBlockInfo
  GetTxInfo
  GetCoins
  ScanCoins
  GenerateUnsignedRawTx
  GenerateSignedRawTx
  SubmitSignedRawTx
  TrackSpentCoins
```

As shown above, run `bin/abel4j-demo` without any arguments will list all the available demos.

### 1.3. Test native library

The Abelian SDK for Java depends on a native library `libabelsdk` built from the Abelian SDK for Go. The native library binaries for all supported platforms (`libabelsdk.so.x` for Linux,`libabelsdk.x.dylib` for macOS) are already included in the SDK jar file `abel4j-x.y.z.jar`.

As of the time of writing, the native library is built for the following platforms:
- Linux x86
- Linux arm64
- MacOS x86
- MacOS arm64 (Apple Silicon)

The SDK jar file contains all the above native libraries for the supported platforms. When you run the demos, the SDK will automatically load the native library compatible with your platform.

Run the following command to test if the native library included in the SDK jar file is compatible with your platform.

```bash
bin/abel4j-demo Crypto
```

The expected output should be like this:

```text
demoName = Crypto
demoArgs = []
default.chain = testnet

==> Generate crypto seeds.
--> Seed[0] = [132 bytes|0x00000000DFBE10E4FCD8FFB8F1F3EF5A...44024F1FF3D7011220EFB9A4F3C90EDA]
--> Seed[1] = [132 bytes|0x0000000092E7E2D4BE673639D59CB6E7...4F827B5F9B7BA2BAAD9FB15B207C4119]
--> Seed[2] = [132 bytes|0x0000000055DCB1813AEEEA1BD03FA053...1C4333A827A46187FDAF75EC9216C90C]

==> Generate crypto keys and addresses.
--> Seed[0]
    Seed = [132 bytes|0x00000000DFBE10E4FCD8FFB8F1F3EF5A...44024F1FF3D7011220EFB9A4F3C90EDA]
    SpendSecretKey = [1540 bytes|0x0000000028B828B4F68E39A2C2A86190...501534A2B9103DE7C6B232769928C284]
    SerialNoSecretKey = [1060 bytes|0x00000000609B828D70BFB4640431049F...C2BE4C2B6B078042592CECAF6C5EBEF2]
    ViewSecretKey = [2408 bytes|0x00000000010000000B037D7E3A1DCA8A...CE50929E27BFD8B288360E8E296EBF77]
    CryptoAddress = [10696 bytes|0x0000000023B834EA17A2682EC80A9433...21724F48950AD1029F67288923A53005]
--> Seed[1]
    Seed = [132 bytes|0x0000000092E7E2D4BE673639D59CB6E7...4F827B5F9B7BA2BAAD9FB15B207C4119]
    SpendSecretKey = [1540 bytes|0x00000000F2C4EA89B89EC788680112B2...082AFF37CC5695825341ACBB110F30A3]
    SerialNoSecretKey = [1060 bytes|0x000000000CF9FF22696EDD18CFC8A21E...5FC5323FD4D941C691F5D1535F8C891A]
    ViewSecretKey = [2408 bytes|0x000000000100000066C44D38547C8101...FBFB5BF9B34BF889ADDB50E8456850E8]
    CryptoAddress = [10696 bytes|0x00000000BDAFDD346C0FA9A0B53AE086...5B8AD385460D171DEEF684C0455E5ECE]
--> Seed[2]
    Seed = [132 bytes|0x0000000055DCB1813AEEEA1BD03FA053...1C4333A827A46187FDAF75EC9216C90C]
    SpendSecretKey = [1540 bytes|0x00000000BA6EA96078B81AF86AF5DEAA...2403C1672000013D7C40EC2E70A086AE]
    SerialNoSecretKey = [1060 bytes|0x000000009A846D6E662B7F29FB8EC0EB...AB8E7FDEA242640DF41528A41AFB6AD7]
    ViewSecretKey = [2408 bytes|0x000000000100000095810B7F8581E0C3...92DA1B1A3DD1E244ECF7C3D00AD14BA7]
    CryptoAddress = [10696 bytes|0x00000000ADD40C21F358097C57B87B91...E201BFE5752A8159803574C2F26B5C0D]

==> Generate crypto keys and addresses again (from the same seeds).
--> Seed[0]
    Seed = [132 bytes|0x00000000DFBE10E4FCD8FFB8F1F3EF5A...44024F1FF3D7011220EFB9A4F3C90EDA]
    SpendSecretKey = [1540 bytes|0x0000000028B828B4F68E39A2C2A86190...501534A2B9103DE7C6B232769928C284]
    SerialNoSecretKey = [1060 bytes|0x00000000609B828D70BFB4640431049F...C2BE4C2B6B078042592CECAF6C5EBEF2]
    ViewSecretKey = [2408 bytes|0x00000000010000000B037D7E3A1DCA8A...2B4DB6CDC583FD3D0039549DFC4AA38C]
    CryptoAddress = [10696 bytes|0x0000000023B834EA17A2682EC80A9433...21724F48950AD1029F67288923A53005]
--> Seed[1]
    Seed = [132 bytes|0x0000000092E7E2D4BE673639D59CB6E7...4F827B5F9B7BA2BAAD9FB15B207C4119]
    SpendSecretKey = [1540 bytes|0x00000000F2C4EA89B89EC788680112B2...082AFF37CC5695825341ACBB110F30A3]
    SerialNoSecretKey = [1060 bytes|0x000000000CF9FF22696EDD18CFC8A21E...5FC5323FD4D941C691F5D1535F8C891A]
    ViewSecretKey = [2408 bytes|0x000000000100000066C44D38547C8101...E91753573DC5CD65256F37DC826646DE]
    CryptoAddress = [10696 bytes|0x00000000BDAFDD346C0FA9A0B53AE086...5B8AD385460D171DEEF684C0455E5ECE]
--> Seed[2]
    Seed = [132 bytes|0x0000000055DCB1813AEEEA1BD03FA053...1C4333A827A46187FDAF75EC9216C90C]
    SpendSecretKey = [1540 bytes|0x00000000BA6EA96078B81AF86AF5DEAA...2403C1672000013D7C40EC2E70A086AE]
    SerialNoSecretKey = [1060 bytes|0x000000009A846D6E662B7F29FB8EC0EB...AB8E7FDEA242640DF41528A41AFB6AD7]
    ViewSecretKey = [2408 bytes|0x000000000100000095810B7F8581E0C3...A04EE6AFF39781967601D708A8FA2D2C]
    CryptoAddress = [10696 bytes|0x00000000ADD40C21F358097C57B87B91...E201BFE5752A8159803574C2F26B5C0D]
```

If the `Crypto` demo runs successfully, then the native library included in the SDK jar file is compatible with your platform.

Note that this demo is for testing purpose only. It invokes low-level cryptographic APIs of the native library which is unlikely to be used directly in your applications. Therefore, it doesn't matter if you don't understand the output here or its source code. The only important thing is whether the demo runs successfully.

### 1.3 Test the default Abec full node for the demos

The Abelian SDK for Java depends on an Abec full node to access the data on the Abelian blockchain. In the default settings, the demos will connect to an Abec full node maintained by the Abelian Foundation. It allows you to run the demos without setting up your own Abec full node.

Note that the default Abec full node is for testing purpose only. It is not stable and may be reset at any time. **For production use, you should always set up your own Abec full node for real-world applications.**

Run the following command to test if the default Abec full node is accessible from your machine.

```bash
bin/abel4j-demo AbecRPCClient
```

The expected output should be like this:

```text
demoName = AbecRPCClient
demoArgs = []
default.chain = testnet

==> Call any RPC method using client.call(method, ...params).

--> Call without parameters.
Request: method: getinfo, params: []
Response: {id: 2869ab40-871a-4c40-b273-d7f936e3302d, result: {"version":120500,"protocolversion":70002,"blocks":250120,"bestblockhash":"efc7470328364ff6747adff00a77846bb1bdc813d0a04b083af350cbcbbc1346","worksum":"35573957182947495035841336086341706532256","timeoffset":0,"connections":0,"proxy":"","difficulty":1,"testnet":true,"relayfee":1.0E-6,"errors":"","nodetype":"SemiFullNode","witnessserviceheight":0}, error: null}
    ✅ This is a successful call.
    Error: null
    Result: {"version":120500,"protocolversion":70002,"blocks":250120,"bestblockhash":"efc7470328364ff6747adff00a77846bb1bdc813d0a04b083af350cbcbbc1346","worksum":"35573957182947495035841336086341706532256","timeoffset":0,"connections":0,"proxy":"","difficulty":1,"testnet":true,"relayfee":1.0E-6,"errors":"","nodetype":"SemiFullNode","witnessserviceheight":0}

--> Call with parameters.
Request: method: getblockhash, params: [1 items|0]
Response: {id: a34fe402-0a2b-45b9-945b-cde9a2dae108, result: "eb143c8328e3131a4474ee1811d3c3a9f27e5102064148dc172966ccb50c2e2b", error: null}
    ✅ This is a successful call.
    Error: null
    Result: "eb143c8328e3131a4474ee1811d3c3a9f27e5102064148dc172966ccb50c2e2b"

--> Call with wrong method name.
Request: method: getnothing, params: []
Response: {id: 126ffa5c-4584-4867-94f9-b767a0a0d186, result: null, error: (-32601, "Method not found")}
    ❌ This is a failed call.
    Error: (-32601, "Method not found")
    Result: null

--> Call with wrong parameters.
Request: method: getinfo, params: [2 items|0, [9 chars|0x0000000]]
Response: {id: b883a361-d6c0-4fac-9dd0-3eeb3e74fba5, result: null, error: (-32602, "wrong number of params (expected 0, received 2)")}
    ❌ This is a failed call.
    Error: (-32602, "wrong number of params (expected 0, received 2)")
    Result: null

==> Call builtin member methods of AbecRPCClient.

--> Get chain info by client.getChainInfo().
ChainInfo: {"rpc":"https://testnet-rpc-exchange.abelian.info","height":250120,"difficulty":1}

==> Get block hash by client.getBlockHash(height=835).
BlockHash: [32 bytes|0x7AF4230AB3E7105C4067B0AEE93D20FBCBEC90A6F9DE08DB2E7AD14D8C77E730]

==> Get block info by client.getBlockInfo(hash=[32 bytes|0x7AF4230AB3E7105C4067B0AEE93D20FBCBEC90A6F9DE08DB2E7AD14D8C77E730]).
BlockInfo: {"height":835,"hash":{"data":"7AF4230AB3E7105C4067B0AEE93D20FBCBEC90A6F9DE08DB2E7AD14D8C77E730","length":32},"prevHash":{"data":"07340CEE74926B2AC5F9C3FA0FBC039CD3E26F8E8844C8902B4EAB04688475A5","length":32},"time":1707375349,"txHashes":[{"data":"4F5D4A6B922FC01E7584016BF3AB92421C7EC881837A18B4B787D03D708990B4","length":32}]}

==> Get tx info by client.getTxInfo(txid=[32 bytes|0x7AF4230AB3E7105C4067B0AEE93D20FBCBEC90A6F9DE08DB2E7AD14D8C77E730]).
TxInfo (without inputs and outputs): {"txid":{"data":"4F5D4A6B922FC01E7584016BF3AB92421C7EC881837A18B4B787D03D708990B4","length":32},"time":1707375349,"blockHash":{"data":"7AF4230AB3E7105C4067B0AEE93D20FBCBEC90A6F9DE08DB2E7AD14D8C77E730","length":32},"blockTime":1707375349,"vins":[],"vouts":[],"confirmations":249286,"coinSerialNumbers":[]}
Inputs:
  vins[0]:
    serialnumber: [64 bytes|0x00000000000000000000000000000000...00000000000000000000000000000000]
Outputs
  vouts[0]:
    script: [20469 bytes|0x01000000FDEE4F9DB62D1CEB38299C21...765AEF4A9875FD4A1231A1E3F1A7AF39]
    fingerprint: [32 bytes|0x15D6D39BE0574F7FE3FBCE7EF6238B5B44075BC90896DD4E1EFD6355729E1842]
```

If the `AbecRPCClient` demo runs successfully, then the default Abec full node is accessible from your machine.

> **Milestone**
> 
> If you have reached this point, congratulations! You have successfully installed the Abelian SDK for Java and tested if it is working properly. You can now learn how to use the SDK to develop your own applications. In the following section, we will omit some output of the demos for brevity. You can run the demos yourself to see the full output.

## 2. Understand account in Abelian SDK

### 2.1 Concepts

An account in the Abelian SDK (both for Java and Go) consists of the following components:

- **Private Key**: A 132-byte random data used to generate all other keys and addresses of the account. It is the only thing you need to back up for an account. You can use the same seed to restore the account. It is also referred to as `Crypto Seed` in the low-level APIs of the Abelian SDK.

- **Spend Key**: A 1540-byte *secret key* to sign transactions and **should be treated as sensitive as the private key**. However, we don't need to back it up as it can be derived from the private key.

- **Serial Number Key**: A 1060-byte *semi-secret key* to decode which input coins are actually spent in a transaction. It can be derived from the private key.

- **View Key**: A 2408-byte *semi-secret key* to decode the amount of ABELs in the output coins belonging to the account. It can be derived from the private key.

- **Abel Address**: A 10696-byte *address* to receive ABELs. We often referred to it as `Address` for short. It can be derived from the private key.

- **Short Address**: A 66-byte *address* to receive ABELs. It is calculated from the abel address and can be used as a shorter alternative to the lengthy abel address. Note that a short address must be registered on the Abelian Name Service (ANS) before it can be used to receive ABELs.

- **Fingerprint**: A 32-byte *fingerprint* of the account. It is calculated from the abel address and is part of the short address (from byte 3 to byte 34). We can use the fingerprint as an unique identifier of the account (aka `Account ID`) because it can be extracted from both the abel address and the short address. However, in this document, we will keep using the short address as the `Account ID` for consistency.

> **Note**
> 
> **Serial Number Key** or **View Key** are called *semi-secret keys* because revealing them to untrusted parties will only compromise the privacy of the account. The funds in the account are safe as long as the **Spend Key** and the **Private Key** is not revealed.
>
> For trusted parties (e.g., an internal Abelian Super Node), the serial number key and the view key can be revealed to them to allow them to track the spent coins and the balance of the account. In this sense, they can be treated as **public keys**.
> 
> In the SDK, an account with the **secret keys** (i.e., **private key** and **spend key**) is called a `signer account`; an account without the **secret keys** is called a `viewer account`.

### 2.2. Create, import and export accounts

The `Account` demo shows how to create, import and export accounts:

```bash
bin/abel4j-demo Account
```

### 2.3. Built-in accounts

There are 10 built-in accounts that will be used in the following demos. Their addresses are exported to `$HOME/.abel4j/accounts` by the `Account` demo. The exported file for an account has an `.abeladdress` extension which can be loaded by the Abelian Desktop Wallet for specifying the receiver when sending ABELs. Each file contains two lines with the first line being the abel address and the second line being the short address.

The built-in accounts will be used in the following demos illustrating the key functionalities required by a non-trivial application (e.g., an exchange wallet).

## 3. Separate cold data and hot data to support offline signing

### 3.1. Initialize wallet databases

The `WalletDB` demo shows how to initialize wallet databases:

```bash
bin/abel4j-demo WalletDB
```

If you haven't run other demos that may change the databases, the output should be like this:

```text
demoName = WalletDB
demoArgs = []
default.chain = testnet

==> Get table summary of the hot wallet db.
Count of accounts: 10
Count of coins   : 0
Count of txs     : 0

==> Get table summary of the cold wallet db.
Count of accounts: 10
```

The `WalletDB` demo creates two wallet databases in `sqlite3` format under `$HOME/.abel4j`: a *cold database* named `demo-testnet-cold-wallet.db` and a *hot database* named `demo-testnet-hot-wallet.db`. You may use any `sqlite3` GUI client or the `sqlite3` command to view the content of the databases.

### 3.2. Cold Database

The *cold database* only stores the *private keys* of the managed accounts for offline signing. It contains only one table `signer_account`:

```bash
sqlite3 $HOME/.abel4j/demo-testnet-cold-wallet.db
``` 

```sql
sqlite> .tables
signer_account
``` 

```sql
sqlite> .schema signer_account
CREATE TABLE `signer_account` (`shortAddressHex` VARCHAR , `privateKeyHex` VARCHAR , PRIMARY KEY (`shortAddressHex`) );
```

```sql
sqlite> select * from signer_account;
```

The truncated output of the above query is:

```text
ABE31C80....337C|00000000F255....DA5E
ABE39759....7111|00000000B283....1F50
ABE32184....94BA|00000000E549....CE21
ABE3B927....63DF|00000000669D....1457
ABE3A161....E270|000000006BEE....751F
ABE38506....1C8B|0000000017F1....F2CF
ABE30206....EF9F|000000000955....6B32
ABE3C872....FAA6|000000000FB7....0F48
ABE34746....8A1C|000000007820....31DD
ABE377B8....7A70|00000000882A....4B90
```

In the `signer_account` table, the `shortAddressHex` column serves as the account ID and the `privateKeyHex` column serves is the private key of the account. Other information of an account will be derived on the fly from the private key when needed.

### 3.3. Hot Database

The *hot database* stores all the data **excluding secret keys** of the managed accounts for online operations such as scanning UTXOs, tracking spent coins, generating unsigned raw transactions, etc. It contains three tables `viewer_account`, `coin` and `tx`:

```bash
sqlite3 ~/.abel4j/demo-testnet-hot-wallet.db
``` 

```sql
sqlite> .tables
coin  tx    viewer_account
``` 

```sql
sqlite> .schema viewer_account
CREATE TABLE `viewer_account` (`shortAddressHex` VARCHAR , `serialNoKeyHex` VARCHAR , `viewKeyHex` VARCHAR , `addressHex` VARCHAR , PRIMARY KEY (`shortAddressHex`) );
```

```sql
sqlite> .schema coin
CREATE TABLE `coin` (`coinIDStr` VARCHAR , `ownerShortAddressHex` VARCHAR , `value` BIGINT , `blockHeight` BIGINT , `snHex` VARCHAR , `isSpent` BOOLEAN , PRIMARY KEY (`coinIDStr`) );
```

```sql
sqlite> .schema tx
CREATE TABLE `tx` (`txMd5` VARCHAR , `unsignedRawTxDataHex` VARCHAR , `signerShortAddresses` VARCHAR , `signedRawTxDataHex` VARCHAR , `txid` VARCHAR , `isSubmitted` BOOLEAN , PRIMARY KEY (`txMd5`) );
```

The `viewer_account` table stores all information but the **secret keys** of the managed accounts. The schema of the other two tables will be explained when they are used in the following demos.

## 4. Manage coins in the hot wallet

### 4.1. Search blocks for viewable coins

The `ScanCoins` demo shows how to scan for coins belonging to the accounts managed by the hot wallet:

```bash
bin/abel4j-demo ScanCoins 5115 5120
```

We choose the above range of block heights because we know in advance that they contain some transactions that send ABELs to the built-in accounts. The `ScanCoins` demo will scan the blocks in the specified range and store the coins belonging to the managed accounts in the hot wallet database.

Let's check the status of the hot wallet database after running the **ScanCoins** demo:

```bash
bin/abel4j-demo WalletDB
```

The output should be like this:

```text
demoName = WalletDB
demoArgs = []
default.chain = testnet

==> Get table summary of the hot wallet db.
Count of accounts: 10
Count of coins   : 1
Count of txs     : 0

==> Get table summary of the cold wallet db.
Count of accounts: 10
```

We can see that the `ScanCoins` demo has scanned 6 blocks and found 1 coin belonging to the managed accounts. Let's further check the content of the `coin` table:

```bash
sqlite3 ~/.abel4j/demo-testnet-hot-wallet.db
``` 

```sql
sqlite> select coinIDStr, ownerShortAddressHex, value, blockHeight, isSpent from coin;
```

The output of the above query is (truncated):

```text
033B8968...A51EFBE0:1|ABE31C80...FC8A337C|19103083|5116|0
```

We define `Coin ID` as the combination of the `txid` and the `vout` index of a coin in hex format. The definition ensures that each coin has a unique ID and can be easily located by the ID.

To understand the meaning of the fields, let's take a closer look at the first coin: it bloings to one of the built-in accounts (`ABE31C80...FC8A337C`), has a value of 1.9103083 ABELs (19,103,083 Neutrinos), is in block `5116` and is not being detected as spent yet.

We now scan another range of blocks to see if there are more coins belonging to the managed accounts:

```bash
bin/abel4j-demo ScanCoins 5750 5770
```

It is supposed to find another 2 coins. Let's check the status of the hot wallet database again:

```bash
bin/abel4j-demo WalletDB
```

The output should be like this:

```text
demoName = WalletDB
demoArgs = []
default.chain = testnet

==> Get table summary of the hot wallet db.
Count of accounts: 10
Count of coins   : 3
Count of txs     : 0

==> Get table summary of the cold wallet db.
Count of accounts: 10
```

> **Note**
> 
> We can scan the same blocks multiple times without worrying about duplicate coins. The `ScanCoins` demo will only add new coins to the database and ignore the duplicate ones. You may verify this by running the following commands to see if the number of coins in the hot wallet database changes:

```bash
bin/abel4j-demo ScanCoins 5760 5770
bin/abel4j-demo WalletDB
```

### 4.2. Track spent coins

At this point, the **isSpent** field of all the coins in the hot wallet database is still `0`(`false`) because we haven't scanned the blocks that contain the transactions spending the coins. The `TrackSpentCoins` demo shows how to track the spent coins:

```bash
bin/abel4j-demo TrackSpentCoins 9398
```

The output should be like this:

```text
demoName = TrackSpentCoins
demoArgs = [9398]
default.chain = testnet

==> Tracking spent coins in block 9398.

--> Tracking spent coins in tx [32 bytes|0x58C16A37383B1C06C488DF0733E9DC1ACBED314E4BEF72B842B934B47A48BE3F].

--> Tracking spent coins in tx [32 bytes|0xFDF55398C6D82EF31FC2316BC3A67B419E75A2C9C0DD51746CA5EFADE692EE28].
💰 Found spent coin: id=033B89683B84DFF7B2E2C05C995A75B3F13CFF3A6FE26841A37B90EFA51EFBE0:1, value=19103083.
```

We can see that there is 1 coin spent in block 9398. Let's check the `coin` table again:

```bash
sqlite3 ~/.abel4j/demo-testnet-hot-wallet.db
``` 

```sql
sqlite> select coinIDStr, blockHeight, isSpent from coin;
```

The output of the above query is:

```text
033B89683B84DFF7B2E2C05C995A75B3F13CFF3A6FE26841A37B90EFA51EFBE0:1|5116|1
31C796C2424BDE46750677DED4D6832204F096AE93A8E58AE6A881CAEE1202F7:1|5751|0
B61E8F894D11D6E4C9CC06FDF967B81065D56778C560F19796C4DA7A47975995:0|5760|0
```

We can see there is 1 coin previously found in block 5116 are now marked as spent. The `isSpent` field of the other coins remain unchanged.

## 5. Make a transaction

Making a transaction is non-trivial task. It involves many steps such as selecting coins to spend, calculating the change, generating the raw transaction, signing the raw transaction, broadcasting the signed transaction, etc. However, it is pretty easy to make a transaction with our crafted demos.

### 5.1. Generate an unsigned raw transaction

The `GenerateUnsignedRawTx` demo shows how to generate an unsigned raw transaction:

```bash
bin/abel4j-demo GenerateUnsignedRawTx
```

> **Note**
>
> Please record the value of `txMd5` in the output for later use.

It will generate an unsigned raw transaction with 2 inputs and 2 outputs. The input coins are selected from the coins in the hot wallet database where the **isSpent** field is `0`(`false`). The output coins are generated by the demo itself. Specifically, the receiver addresses are randomly chosen from the built-in accounts and the total output value is calculated by summing up the values of the input coins and subtracting the transaction fee from it.

The generated unsigned raw transaction will be saved in the `tx` table of the hot wallet database. Let's check the status of the hot wallet database after running the `GenerateUnsignedRawTx` demo:

```bash
bin/abel4j-demo WalletDB
```

The output should be like this:

```text
demoName = WalletDB
demoArgs = []
default.chain = testnet

==> Get table summary of the hot wallet db.
Count of accounts: 10
Count of coins   : 3
Count of txs     : 1

==> Get table summary of the cold wallet db.
Count of accounts: 10
```

Note that the count of records in the `tx` table is `1` now. Let's check the content of the `tx` table:

```bash
sqlite3 ~/.abel4j/demo-testnet-hot-wallet.db
``` 

```sql
sqlite> select txMd5, length(unsignedRawTxDataHex), length(signedRawTxDataHex), isSubmitted from tx;
```

The output of the above query is like this (you may see different values):

```text
F99C8BD32950DFB4C27651B8B1D85519|494436||0
```

If you have recorded the value of `txMd5` in the output of the `GenerateUnsignedRawTx` demo, you can verify that it is the same as the value in the `tx` table. We can also see that the value of `unsignedRawTxDataHex` is not empty and the value of `signedRawTxDataHex` is empty. This is because the signed raw transaction is not generated yet.

### 5.2. Sign the unsigned raw transaction

The `GenerateSignedRawTx` demo shows how to sign the unsigned raw transaction generated by the `GenerateUnsignedRawTx` demo:

```bash
bin/abel4j-demo GenerateSignedRawTx F99C8BD32950DFB4C27651B8B1D85519
```

The output should be like this (it's unlikely to be the same as yours):

```text
demoName = GenerateSignedRawTx
demoArgs = [F99C8BD32950DFB4C27651B8B1D85519]
default.chain = testnet

==> Generating a signed raw tx with txMd5: F99C8BD32950DFB4C27651B8B1D85519

==> Getting unsigned raw tx data from the hot wallet db.
Got unsigned raw tx: txMd5=F99C8BD32950DFB4C27651B8B1D85519, data=[247218 bytes|0x02651DCB7A4E484157DC2F06F0176CE3...01D134171AFE337FA902FE40420F0000], signers=2.

==> Getting signer accounts from the cold wallet db.
Got 2 signer accounts:
Signer 0: [66 bytes|0xABE12184DF76D1C0FEB878E54F0FF390...B5C34A6D176A2DDB7D8DB03261EA392C]
Signer 1: [66 bytes|0xABE19759B3B1FE374681E82ACF5742AA...010A0FE3DFF275BE226A2DC60D9F3D09]

==> Signing the unsigned raw tx.
Signed raw tx: txid=[32 bytes|0x381E6311F18787A7A26AF9657984DE6748FBB1C6ED7495F562398BF10242114E], data=[1798256 bytes|0x010000000240D3D88382D20C5ADE42B1...E97EA3294D5C6D3B4B248FF0DD7245E1].

==> Saving the signed raw tx to the hot wallet db.
```

We pass the `txMd5` as the argument to the demo. It will be used to find the unsigned raw transaction in the `tx` table. The demo will sign the transaction and save the signed data back to the `tx` table.

Let's check the status of the hot wallet database after running the `GenerateSignedRawTx` demo:

```bash
sqlite3 ~/.abel4j/demo-testnet-hot-wallet.db
``` 

```sql
sqlite> select txMd5, length(unsignedRawTxDataHex), length(signedRawTxDataHex), isSubmitted from tx;
```

Now the output of the above query is like this (you may see different values):

```text
F99C8BD32950DFB4C27651B8B1D85519|494436|3596512|0
```

We can see that the value of `signedRawTxDataHex` is not empty now.

> **Note**
>
> The transaction was signed by the spend keys of the senders (who owns the input coins). The spend keys are generated on-demand from the private keys stored in the cold wallet database. The private keys are never exposed to the public network. This is how the cold wallet database protects the private keys from being stolen.

### 5.3. Broadcast the signed raw transaction

The last step is to broadcast the signed raw transaction to the Abelian network. The `SubmitSignedRawTx` demo shows how to broadcast the signed raw transaction generated by the `GenerateSignedRawTx` demo:

```bash
bin/abel4j-demo SubmitSignedRawTx F99C8BD32950DFB4C27651B8B1D85519
```

If the transaction is successfully submitted, please record the value of `txid` in the output and search it on the [Abelian Explorer](https://explorer.abelian.info) to see if it is confirmed. You may also check the `tx` table to see if the `isSubmitted` field of the transaction is set to `1`(`true`).

If the transaction fails to be submitted, please check the error message in the output. The most common error is that one or more coins in the inputs are spent before the transaction is submitted. If you know at which block height the coins are spent, you can run the `TrackSpentCoins` demo to update the `isSpent` field of the coins in the hot wallet database and try to make another unsigned raw transaction, sign it and submit it again.

## 6. Summary

In this document, we have learned how to install the Abelian SDK for Java, test if it is working properly, understand the concepts of account in the Abelian SDK, separate cold data and hot data to support offline signing, manage coins in the hot wallet and make a transaction with the crafted demos.

There are other demos in the SDK that are not covered in this document. You may run them yourself to see the results. You may also read the source code of the demos to learn how to use the SDK to develop your own applications.