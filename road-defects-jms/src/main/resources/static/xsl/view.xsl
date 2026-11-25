<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" encoding="UTF-8" indent="yes" doctype-system="about:legacy-compat"/>

    <xsl:template match="/">
        <html lang="ru">
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>REST API</title>
                <link rel="stylesheet" href="/css/style.css"/>
            </head>
            <body>
                <header class="topbar">
                    <div class="topbar-inner">
                        <span class="brand">Дефекты дорожного покрытия: REST API</span>
                        <nav>
                            <a href="/api/defects">Дефекты</a>
                            <a href="/api/types">Типы дефектов</a>
                            <a href="/defects">Обычный интерфейс</a>
                        </nav>
                    </div>
                </header>
                <main>
                    <xsl:apply-templates/>
                </main>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="/defects">
        <xsl:choose>
            <xsl:when test="type">
                <h1>Дефекты типа <span class="code"><xsl:value-of select="type"/></span></h1>
                <p class="links">
                    <a href="/api/types/{type}">Тип <xsl:value-of select="type"/></a>
                    <a href="/api/defects">Все дефекты</a>
                </p>
            </xsl:when>
            <xsl:otherwise>
                <h1>Дефекты</h1>
            </xsl:otherwise>
        </xsl:choose>
        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Тип</th>
                        <th>Широта</th>
                        <th>Долгота</th>
                        <th>Дата и время</th>
                    </tr>
                </thead>
                <tbody>
                    <xsl:apply-templates select="defect" mode="row"/>
                    <xsl:if test="not(defect)">
                        <tr><td colspan="5" class="empty">Дефектов нет.</td></tr>
                    </xsl:if>
                </tbody>
            </table>
        </div>
    </xsl:template>

    <xsl:template match="defect" mode="row">
        <tr>
            <td><a href="/api/defects/{id}"><xsl:value-of select="id"/></a></td>
            <td>
                <a href="/api/types/{typeCode}"><span class="code"><xsl:value-of select="typeCode"/></span></a>
                <xsl:text> </xsl:text>
                <xsl:value-of select="typeName"/>
            </td>
            <td><xsl:value-of select="latitude"/></td>
            <td><xsl:value-of select="longitude"/></td>
            <td><xsl:call-template name="datetime"><xsl:with-param name="value" select="detectedAt"/></xsl:call-template></td>
        </tr>
    </xsl:template>

    <xsl:template match="/defect">
        <h1>Дефект <xsl:value-of select="id"/></h1>
        <div class="table-wrap">
            <table class="details">
                <tr><th>ID</th><td><xsl:value-of select="id"/></td></tr>
                <tr>
                    <th>Тип</th>
                    <td>
                        <a href="/api/types/{typeCode}"><span class="code"><xsl:value-of select="typeCode"/></span></a>
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="typeName"/>
                    </td>
                </tr>
                <tr><th>Широта</th><td><xsl:value-of select="latitude"/></td></tr>
                <tr><th>Долгота</th><td><xsl:value-of select="longitude"/></td></tr>
                <tr>
                    <th>Дата и время</th>
                    <td><xsl:call-template name="datetime"><xsl:with-param name="value" select="detectedAt"/></xsl:call-template></td>
                </tr>
            </table>
        </div>
        <p class="links">
            <a href="/api/defects">Все дефекты</a>
            <a href="/api/types/{typeCode}/defects">Дефекты типа <xsl:value-of select="typeCode"/></a>
        </p>
    </xsl:template>

    <xsl:template match="/types">
        <h1>Типы дефектов</h1>
        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>Код</th>
                        <th>Название</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <xsl:for-each select="type">
                        <tr>
                            <td><a href="/api/types/{code}"><span class="code"><xsl:value-of select="code"/></span></a></td>
                            <td><xsl:value-of select="name"/></td>
                            <td><a href="/api/types/{code}/defects">Дефекты этого типа</a></td>
                        </tr>
                    </xsl:for-each>
                    <xsl:if test="not(type)">
                        <tr><td colspan="3" class="empty">Типов нет.</td></tr>
                    </xsl:if>
                </tbody>
            </table>
        </div>
    </xsl:template>

    <xsl:template match="/type">
        <h1>Тип <span class="code"><xsl:value-of select="code"/></span></h1>
        <div class="table-wrap">
            <table class="details">
                <tr><th>Код</th><td><xsl:value-of select="code"/></td></tr>
                <tr><th>Название</th><td><xsl:value-of select="name"/></td></tr>
            </table>
        </div>
        <p class="links">
            <a href="/api/types/{code}/defects">Дефекты этого типа</a>
            <a href="/api/types">Все типы</a>
        </p>
    </xsl:template>

    <xsl:template match="/error">
        <h1>Ошибка <xsl:value-of select="status"/></h1>
        <xsl:for-each select="message">
            <p class="error"><xsl:value-of select="."/></p>
        </xsl:for-each>
        <p class="links"><a href="/api/defects">К списку дефектов</a></p>
    </xsl:template>

    <xsl:template name="datetime">
        <xsl:param name="value"/>
        <xsl:value-of select="concat(substring($value, 9, 2), '.', substring($value, 6, 2), '.',
                                     substring($value, 1, 4), ' ', substring($value, 12))"/>
    </xsl:template>

</xsl:stylesheet>
