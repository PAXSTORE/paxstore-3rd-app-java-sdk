# PAXSTORE 3rd App Java SDK [ ![Download](https://api.bintray.com/packages/paxstore-support/paxstore/paxstore-3rd-app-java-sdk/images/download.svg?version=5.02.02) ](https://bintray.com/paxstore-support/paxstore/paxstore-3rd-app-java-sdk/5.02.02/link)

PAXSTORE 3rd App Java SDK provides the basic java APIs for third party developers to integrate their apps on PAXSTORE.

## Download Dependency

Gradle:

    implementation 'com.pax.market:paxstore-3rd-app-java-sdk:5.02.02'

## API Usage

### Download param(Optional, ignore this part if you don't have download parameter requirement)
    String packageName = context.getPackageName();
    int versionCode = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
    String saveFilePath = getFilesDir() + "/YourPath/";                                                             //Specifies the download path for the parameter file

    ParamApi paramApi = new ParamApi(apiUrl, appKey, appSecret, terminalSerialNo)
    paramApi.downloadParamToPath(getApplication().getPackageName(), BuildConfig.VERSION_CODE, saveFilePath);

### Upload subsidiary device information(subsidiary device info or application info)
    TerminalSyncInfo terminalSyncInfo = new TerminalSyncInfo();             //Init terminal Sync Info
    terminalSyncInfo.setName("Q20");                                        //Set Name，mandatory
    terminalSyncInfo.setType(SyncApi.SyncType.DEVICE);                      //Set Type [Application Info:1, Device Info: 2, Hardware Info: 3, Install History Info: 4]，mandatory
    terminalSyncInfo.setRemarks("TEST");                                    //Set Remarks, Optional
    terminalSyncInfo.setVersion("1.0");                                     //Set Version，Optional
    terminalSyncInfo.setStatus("");                                         //Set Status，Optional
    terminalSyncInfo.setSyncTime(System.currentTimeMillis());               //Set Sync Time，Optional
    List<TerminalSyncInfo> terminalSyncInfoList = new ArrayList<TerminalSyncInfo>();
    terminalSyncInfoList.add(terminalSyncInfo);
    SyncApi syncApi = new SyncApi(apiUrl, appKey, appSecret, terminalSerialNo);
    String result = SyncApi.syncTerminalInfo(terminalSyncInfoList);