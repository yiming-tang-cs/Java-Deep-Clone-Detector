# Java-Deep-Clone-Detector

## Introduction

This is an Eclipse research tool for detecting Java deep clones.

## Usage

Select analyzed projects -> Quick Assess -> Choose command "Detect java deep clone".

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
public Object clone() {
    try {
        return (Address) super.clone();
    } catch (CloneNotSupportedException e) {
        return new Address(this.street, this.getCity(), this.getCountry());
    }
}
</pre>
</td>
</tr>
</table>
