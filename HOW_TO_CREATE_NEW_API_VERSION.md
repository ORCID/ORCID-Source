# How To Create a New API Version

## The public API

Look for a recent service bean in orcid-t1-web-context.xml. For example, t1OrcidApiServiceImplV1_2_rc1.

Create a new bean definition for your new API version, similar to the existing one. Set the external version property of the bean to your new version number.

If you want your new API version to be served on the root URL, then update the external version property of t1OrcidApiServiceImplRoot to your version number.

Create a new class for your new API version. See existing classes, such as T1OrcidApiServiceImplV1_2_rc1.

There is no longer any need to configure a service delegator and validation manager. It is done automcatically by the base service class.

You now have a new version of the public API configured!

## The member API

Look for a recent service bean in orcid-api-web-context.xml. For example, t2OrcidApiServiceImplV1_2_rc1.

Create a new bean definition for your new API version, similar to the existing one. Set the external version property of the bean to your new version number.

If you want your new API version to be served on the root URL, then update the external version property of t2OrcidApiServiceImplRoot to your version number.

There is no longer any need to configure a service delegator and validation managers. It is done automcatically by the base service class.

Create a new class for your API version. See existing classes such as T2OrcidApiServiceImplV1_2_rc1.

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
