# How To Create a New API Version

## Set up validation manager beans

Look for the existing validation managers in orcid-core-context.xml. For example, outgoingValidationManagerV1_0_15.

There are two validation managers for each API version. One for validating incoming messsages, and one for validating outgoing messages.

Create the new bean definitions for your new API version.

Also, there is a pair of bean aliases for the latest API version (served on the root URL), called incomingValidationManagerLatest and outgoingValidationManagerLatest.

You may want to point these aliases to your new version.

## The public API

Look for existing service and delegator beans in orcid-t1-web-context.xml. For example, t1OrcidApiServiceImplV1_0_15 and t1OrcidApiServiceDelegatorV1_0_15.

Create new bean definitions for your new API version.

Also, there is a bean alias for the latest API version (serviced on the root URL) called t1OrcidApiServiceDelegatorLatest.

You may want to point the alias to your new version.

Create a new class for your new API version. See existing classes, such as T1OrcidApiServiceImplV1_0_15.

You now have a new version of the public API configured!

## The member API

Look for existing service and delegator beans in orcid-t2-web-context.xml. For example, t2OrcidApiServiceImplV1_0_15 and t2OrcidApiServiceDelegatorV1_0_15.

Create new bean definitions for your new API version.

There is also a bean definition for the delegator for the latest API version (served on the root URL) called t2OrcidApiServiceDelegatorLatest. You may want to update the externalVersion property to be your new API version number.

Create a new class for your API version. See existing classes such as T2OrcidApiServiceImplV1_0_15.

You now have a new version of the member API configured!

## Maintain backwards compatibility

To maintain backwards compatibility, you will have to be able to convert between your new version, to the version immediately before.

See for example, OrcidMessageVersionConverterImplV1_0_14ToV1_0_15.

Then add your converter to orcidMessageVersionConverterChain bean in orcid-core-context.xml.

Well done!

## Additional info

There is a class diagram of the versioned public API.

[http://www.gliffy.com/go/publish/4685870](http://www.gliffy.com/go/publish/4685870)

The class structure for the member API is similar.
