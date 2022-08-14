# Error Notifier
## Rationale
Fabric Loader's error handling is not very good, instead, it is **very bad**.

Error Notifier is a simple project to provide a better error screen with little load time.

## Usage
Create a file called `error_notifier.json` in the resources folder.
Here is a simple example that depends on >=1.0.0 and <1.1.0 Fabric API, and breaks Java 17.
```json
{
    "schemaVersion": 1,
    "checks": [
        {
            "type": "depends",
            "modId": "fabric",
            "modName": "Fabric API",
            "versions": ">=1.0.0 <1.1.0",
            "url": "https://www.curseforge.com/minecraft/mc-mods/fabric-api/files/all/"
        },
        {
            "type": "breaks",
            "modId": "java",
            "modName": "Java",
            "versions": "17"
        }
    ]
}
```

### Advanced Usages
Make a class that implements `ErrorNotifier`, then add it using a service (In a file `META-INF/services/me.shedaniel.errornotifier.api.ErrorProvider`).
