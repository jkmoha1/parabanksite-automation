<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<!-- Runs Chrome & Firefox in parallel (2 threads).
     Within each browser, classes run in order; @Test(singleThreaded=true) keeps mutators serialized. -->
<suite name="ParaBank Cross-Browser (Single User Safe)" parallel="tests" thread-count="2" preserve-order="true">
  <listeners>
    <listener class-name="com.parabank.listeners.TestListener"/>
  </listeners>

  <test name="Chrome">
    <parameter name="browser" value="chrome"/>
    <classes>
      <class name="com.parabank.tests.LoginTests"/>
      <class name="com.parabank.tests.RegistrationTests"/>
      <class name="com.parabank.tests.AccountTests"/>
      <class name="com.parabank.tests.TransferTests"/>
      <class name="com.parabank.tests.BillPayTests"/>
      <class name="com.parabank.tests.LoanTests"/>
    </classes>
  </test>

  <test name="Firefox">
    <parameter name="browser" value="firefox"/>
    <classes>
      <class name="com.parabank.tests.LoginTests"/>
      <class name="com.parabank.tests.RegistrationTests"/>
      <class name="com.parabank.tests.AccountTests"/>
      <class name="com.parabank.tests.TransferTests"/>
      <class name="com.parabank.tests.BillPayTests"/>
      <class name="com.parabank.tests.LoanTests"/>
    </classes>
  </test>
</suite>

