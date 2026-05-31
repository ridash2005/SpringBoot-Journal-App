# Contributing to Journal App

First off, thank you for considering contributing to Journal App! It's people like you that make Journal App such a great tool.

## Code of Conduct

This project and everyone participating in it is governed by our [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to [rickaryadas@gmail.com](mailto:rickaryadas@gmail.com).

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the issue list as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

- **Use a clear and descriptive title**
- **Describe the exact steps which reproduce the problem**
- **Provide specific examples to demonstrate the steps**
- **Describe the behavior you observed after following the steps**
- **Explain which behavior you expected to see instead and why**
- **Include screenshots and animated GIFs if possible**
- **Include your environment details** (Java version, OS, etc.)

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

- **Use a clear and descriptive title**
- **Provide a step-by-step description of the suggested enhancement**
- **Provide specific examples to demonstrate the steps**
- **Describe the current behavior and the expected behavior**
- **Explain why this enhancement would be useful**

### Pull Requests

- Fill in the required template
- Follow the Java styleguides
- Include appropriate test cases
- End all files with a newline
- Avoid platform-dependent code

## Development Setup

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/ridash2005/SpringBoot-Journal-App.git
   cd journalApp
   ```

3. Add upstream remote:
   ```bash
   git remote add upstream https://github.com/ridash2005/SpringBoot-Journal-App.git
   ```

4. Create a new branch for your feature:
   ```bash
   git checkout -b feat/your-feature-name
   ```

5. Set up your development environment (see README.md)

## Making Changes

### Code Style

We follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). Key points:

- Use 4 spaces for indentation
- Line length should not exceed 100 characters
- Use meaningful variable names
- Add comments for complex logic
- Document public methods with JavaDoc

### Commit Messages

We follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

Types:
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation changes
- `style`: Code style changes (no logic change)
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Adding or updating tests
- `chore`: Build process or dependencies
- `ci`: CI/CD configuration

Examples:
```
feat: add sentiment analysis for entries
fix: resolve JWT token validation error
docs: update API documentation
test: add unit tests for UserService
```

### Testing

- Write tests for all new features
- Ensure all existing tests pass
- Run tests locally before pushing:
  ```bash
  mvn clean test
  ```

- Test coverage should be > 80% for new code
- Use meaningful test names following pattern: `testWhenConditionThenExpectedResult`

Example test:
```java
@Test
public void testWhenValidUserThenReturnTrue() {
    User user = new User();
    user.setUserName("testuser");
    user.setPassword("password123");
    
    boolean result = userService.saveNewUser(user);
    
    assertTrue(result);
}
```

### Branches and Pull Requests

1. Keep your branch up to date:
   ```bash
   git fetch upstream
   git rebase upstream/master
   ```

2. Push your changes:
   ```bash
   git push origin feat/your-feature-name
   ```

3. Create a Pull Request with:
   - Clear description of changes
   - Reference to related issues (e.g., "Closes #123")
   - Screenshots if UI-related
   - Test results

4. PR Title format:
   ```
   [TYPE] Brief description
   ```
   Example: `[FEATURE] Add sentiment analysis for journal entries`

## Review Process

- At least one maintainer must review the PR
- All CI checks must pass
- Code coverage should not decrease
- Discussion may be needed for architectural decisions

## Reporting Security Vulnerabilities

If you discover a security vulnerability, please email [rickaryadas@gmail.com](mailto:rickaryadas@gmail.com) instead of using the issue tracker. Please do not publicly disclose the vulnerability until it has been addressed.

## Documentation

- Update README.md if adding new features
- Update CONTRIBUTING.md for process changes
- Add code comments for complex logic
- Include JavaDoc for public APIs
- Update CHANGELOG if it exists

## Additional Notes

### Issue and Pull Request Labels

- `bug`: Confirmed bugs or reports that are very likely to be bugs
- `enhancement`: New feature or request
- `documentation`: Improvements or additions to documentation
- `good first issue`: Good for newcomers
- `help wanted`: Extra attention is needed
- `question`: Further information is requested
- `wontfix`: This will not be worked on

### Project Maintainers

The project is maintained by Rickarya Das. Maintainers can merge PRs, manage issues, and make architectural decisions.

## Community

- GitHub Discussions for questions and ideas
- GitHub Issues for bugs and features
- Email for security concerns

## License

By contributing to Journal App, you agree that your contributions will be licensed under its MIT License.

## Recognition

Contributors who have made significant contributions will be recognized in the project README.

Thank you for contributing to Journal App! 🎉
