# Layout Info Web

A simple widget to show technical information about the current page.

Use it to explore the page/layout API, and the various settings that are otherwise hard to discover. Also good for String-Maps and configuration settings, that might not be documented other than in source code.

Currently includes:

* A page's typeSettings (e.g. including the different columns and their contents, meta information)
* A page's description
* PortletPreferences for _all_ of the portlets on the page (great way to figure out the configuration of particular portlets if you want to [embed them statically in a page or theme](https://liferay.dev/blogs/-/blogs/embedding-portlets-and-setting-preferences-in-a-liferay-7-2-theme) without looking at the database)

## Build instructions

* Clone this repository into a Liferay Workspace's `modules` directory
* Originally built with Target Platform 7.2.10.1
* In the past also built with `liferay.workspace.product=dxp-7.3-ga1`
* Currently, version last built with `liferay.workspace.product=dxp-7.4-u102`