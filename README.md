# Java-Deep-Clone-Detector
[![Build Status](https://app.travis-ci.com/yiming-tang-cs/Java-Deep-Clone-Detector.svg?token=gywSHb5G1W81zrovzorQ&branch=master)](https://app.travis-ci.com/yiming-tang-cs/Java-Deep-Clone-Detector)
## Introduction

In the real-world of OOP practice, a growing concern is that developers use duplicate source code when implementing the same functionality, which results in program redundancy and inefficiency. For example, developers implement their programs using deep clones rather than shallow copies. This is an Eclipse research tool for detecting Java deep clones. With this tool, you can take a look at how the deep clone issue shows up in real-life Java projects.

## Usage

### Building from the command line

Before importing it into Eclipse, please make sure you can build it from the command line. The command is `mvn clean verify`.

### How to run in Eclipse
Select analyzed projects -> Quick Assess -> Choose command `Detect java deep clone`.

## Details

Below is a table listing all possible Java deep clone types this tool can detect.

<table>
<tr>
    <td> <b>Clone type</b> </td> <td>  <b>Description</b> </td><td> <b>Code example</b> </td>
</tr>

<tr>
    <td> Cloneable Interface </td><td>By default, the clone() method does a shallow copy. Developers override Object.clone() for deep clone.</td>
    <td>
        <pre>
class A implements Cloneable {
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    ...
}
        </pre>
    </td>
</tr>
    
<tr>
    <td>Copy Constructor</td>
    <td>Clone class by copying constructor</td>
    <td><pre>
public class Order {
    public Order(Order source) {
        number = source.number;
    }
}
-----------------------------------------------------------
for (Order sourceOrder : source.orders) {
    orders.add(new Order(sourceOrder));
}
        </pre>
    </td>
</tr>
	
<tr>
    <td>Serialization</td>
    <td>Using Java serialization</td>
    <td><pre>
public class SerializableClass implements Serializable {
    public SerializableClass deepCopy() throws Exception {
	// Serialization of object
	...
	// De-serialization of object
	...
	return copied;
    }
}
-----------------------------------------------------------
SerializableClass deepCopiedInstance = myClass.deepCopy();
    </pre>
    </td>
</tr>
	
</table>


## Development platform

This tool is developed on Eclipse for RCP and RAP (version [2021-06](https://www.eclipse.org/downloads/packages/release/2021-06/r)) by using Oracle Java ([JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)). It is built on maven (Apache Maven [3.8.2](https://maven.apache.org/docs/3.8.2/release-notes.html)).

## Contact

If you have any additional questions, please contact me via email: <t_yiming@encs.concordia.ca>
