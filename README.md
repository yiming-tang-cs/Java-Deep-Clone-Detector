# Java-Deep-Clone-Detector

## Introduction

This is an Eclipse research tool for detecting Java deep clones.

## Usage

Before importing it into Eclipse, please Make sure you can build it from the command line. The command is `mvn clean test`.

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
