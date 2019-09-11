Thymeleaf Layout Dialect and Thymeleaf Fragments
========================


1. A dialect for Thymeleaf that lets you build layouts and reusable templates in
order to improve code reuse.

2. Thymeleaf Fragments to reuse some common parts of a site.

Docs
----

1. For installation instructions, examples, and configuration options for version 2
of the layout dialect, check out the docs pages over on https://ultraq.github.io/thymeleaf-layout-dialect/

2. Documents for thymeleaf fragments: https://www.thymeleaf.org/doc/articles/layouts.html

3. Some links may be helpful:
https://o7planning.org/vi/12345/thymeleaf
https://www.baeldung.com/spring-thymeleaf-fragments
https://www.baeldung.com/thymeleaf-spring-layouts
https://www.thymeleaf.org/documentation.html

USAGES
-----

Create thymeleaf common fragments for pages using syntax: `th:fragment="name-frament"`
* header:
```html
<div xmlns:th="www.thymeLeaf.org" th:fragment="header">
    <header class="header_area">
        <h1>header</h1>
    </header>
</div>
```
* footer:
```html
<div xmlns:th="www.thymeLeaf.org" th:fragment="footer">
    <header class="footer_area">
        <h1>footer</h1>
    </header>
</div>
```
...

Create a common layout to be used for several pages, defining extension points
in the body with the `layout:fragment`/`data-layout-fragment` processors:

```html
<!DOCTYPE html>
<html lang="en"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">

    <head>
        <title layout:title-pattern="$CONTENT_TITLE">layout page</title>
    </head>

    <body>
        <!-- Header, navbar,...-->
        <div th:replace="fragments/header :: header">This header content is going to be replaced.</div>

        <!-- main content -->
        <div layout:fragment="main-content"></div>

        <!-- footer -->
        <footer th:replace="fragments/footer :: footer" class="footer"></footer>
    </body>

</html>
```

Create a content page that will use the layout, defining any HTML to use for the
extension points in the layout, and specified by a `layout:decorate`/`data-layout-decorate`
processor at the root element of your page:

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      lang="en" xmlns:th="www.thymeLeaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/default-layout}">
<head>
    <title>Content page</title>
    <link rel="stylesheet" type="text/css" th:href="@{/static/css/home/index.css}"/>
</head>
<body>
<div layout:fragment="main-content">
    <h1>You can override layout:fragment="main-content" in default-layout</h1>
</div>
</body>
</html>
```

Get Thymeleaf to process your content page.  The result will be the layout
template decorated by your content page, meaning that the content page will fill
out the layout's extension points, replace titles, and merge `<head>` items:

```html
<!DOCTYPE html>
<html>
<head>
  <title>Content page</title>
</head>
<body>
  <header class="header_area">
      <h1>header</h1>
  </header>
  <div>
      <h1>You can override layout:fragment="main-content" in default-layout</h1>
  </div>
  <footer class="footer_area">
      <h1>footer</h1>
  </footer>  
</body>
</html>
```

Intrigued?  Check out the documentation links near the top of this readme to
learn more.

Hope this help!!!