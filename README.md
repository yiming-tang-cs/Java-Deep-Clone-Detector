# Java-Deep-Clone-Detector
[![Build Status](https://app.travis-ci.com/yiming-tang-cs/Java-Deep-Clone-Detector.svg?token=gywSHb5G1W81zrovzorQ&branch=master)](https://app.travis-ci.com/yiming-tang-cs/Java-Deep-Clone-Detector)
## Introduction

In the real world of OOP practice, a growing concern is that developers use duplicate source code when implementing the same functionality, which results in program redundancy and inefficiency. For example, developers implement their programs using deep clones rather than shallow copies. This is an Eclipse research tool for detecting Java deep clones. With this tool, we can take a look at how the deep clone issue shows up in real-life Java projects.

## Usage

Before importing it into Eclipse, please make sure you can build it from the command line. The command is `mvn clean verify`.

Select analyzed projects -> Quick Assess -> Choose command `Detect java deep clone`.

## Details

Below is a table listing all possible Java deep clone types this tool can detect.

<table>
<tr>
    <td> <b>Clone type</b> </td> <td>  <b>Description</b> </td><td> <b>Code example</b> </td>
</tr>

<tr>
    <td> Cloneable Interface </td><td>Using Object.clone()</td>
    <td>
        <pre>
@Override
protected Object clone() throws CloneNotSupportedException {
    return super.clone();
}
        </pre>
    </td>
</tr>
    
<tr>
    <td>Copy Constructor</td>
    <td>Clone class by copying constructor</td>
    <td><pre>
public Address(Address that) {
    this(that.getStreet(), that.getCity(), that.getCountry());
}
        </pre>
    </td>
</tr>
</table>
